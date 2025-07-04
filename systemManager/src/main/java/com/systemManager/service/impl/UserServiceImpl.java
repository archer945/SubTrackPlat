package com.systemManager.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.BatchUserDTO;
import com.common.domain.dto.systemManager.ResetPasswordDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.dto.systemManager.UserImportDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.Dept;
import com.systemManager.entity.User;
import com.systemManager.entity.UserRole;
import com.systemManager.mapper.DeptMapper;
import com.systemManager.mapper.UserRoleMapper;
import com.systemManager.mapper.ms.UserMsMapper;
import com.systemManager.mapper.UserMapper;
import com.systemManager.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DeptMapper deptMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("userMsMapperImpl")
    private UserMsMapper msMapper;

    @Override
    @Transactional
    public String saveUser(AddUserDTO addUserDTO) {
        if(userMapper.selectUserByUsername(addUserDTO.getUsername())){
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检验部门是否存在
        if (addUserDTO.getDeptId() != 0){
            this.checkDeptExist(addUserDTO.getDeptId());
        }
        // 转换为DO
        User user = msMapper.addDtoToDo(addUserDTO);
        // 插入
        if(userMapper.insert(user) != 1){
            throw new RuntimeException("添加用户数据失败");
        }

        return user.getUserId().toString();
    }

    @Override
    public PageDTO<UserVO> listUser(UserQuery userQuery) {
        // 分页对象
        Page<UserVO> page = new Page<>(userQuery.getPageIndex(), userQuery.getPageSize());
        // 查询
        Page<UserVO> userPage = userMapper.selectUser(userQuery, page);
        return PageDTO.create(userPage);
    }



    @Override
    @Transactional
    public String removeUser(Long id) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        if(user.getUsername().equals("admin")){
            throw new IllegalArgumentException("超级管理员账号不能被删除或禁用");
        }
        user.setStatus(2);
        if (userMapper.updateById(user) == 0) {
            throw new RuntimeException("删除用户失败");
        }
        return id.toString();
    }

    @Override
    public String updateUser(Long id, UpdateUserDTO dto) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 保存原始用户名，用于后续比较
        String originalUsername = user.getUsername();
        
        // 检验部门是否存在
        if (dto.getDeptId() != 0){
            this.checkDeptExist(dto.getDeptId());
        }

        // 将DTO转换为实体
        User updatedUser = msMapper.dtoToDo(dto);
        updatedUser.setUserId(id);
        
        // 使用原始用户名进行比较
        if("admin".equals(originalUsername) && (updatedUser.getStatus() == 0 || updatedUser.getStatus() == 2)){
             throw new IllegalArgumentException("超级管理员账号不能被删除或禁用");
        }
        
        if (userMapper.updateById(updatedUser) == 0) {
            throw new RuntimeException("修改用户失败");
        }
        return id.toString();
    }
    
    @Override
    @Transactional
    public String resetPassword(Long id, ResetPasswordDTO dto) {
        // 查询用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        // 更新密码
        User updateUser = new User();
        updateUser.setUserId(id);
        updateUser.setPassword(encodedPassword);
        
        // 执行更新
        if (userMapper.updateById(updateUser) == 0) {
            throw new RuntimeException("重置密码失败");
        }
        
        return id.toString();
    }

    //为用户分配角色
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有的角色关联
        userRoleMapper.deleteByUserId(userId);

        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        // 构建新的用户角色关联
        List<UserRole> userRoles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(now);
            userRoles.add(userRole);
        }

        // 批量插入新的用户角色关联
        if (!userRoles.isEmpty()) {
            userRoleMapper.batchInsert(userRoles);
        }

        return true;
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);

        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);

        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    //检验部门是否存在
    private void checkDeptExist(Long deptId) {
        if (deptId == null) {
            return;
        }
        boolean exists = deptMapper.exists(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeptId, deptId));

        if (!exists) {
            throw new IllegalArgumentException(
                    String.format("部门(ID:%d)不存在或已被删除", deptId)
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchRemoveUsers(BatchUserDTO dto) {
        List<Long> userIds = dto.getUserIds();
        
        // 检查是否包含admin用户
        List<User> users = userMapper.selectBatchIds(userIds);
        for (User user : users) {
            if ("admin".equals(user.getUsername())) {
                throw new IllegalArgumentException("超级管理员账号不能被删除或禁用");
            }
        }
        
        // 批量更新用户状态为失效(2)
        userMapper.batchUpdateStatus(userIds, 2);
        
        return "批量删除成功，共删除" + userIds.size() + "个用户";
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importUsers(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行
            int rowIndex = 1;
            while (rowIndex <= sheet.getLastRowNum()) {
                Row row = sheet.getRow(rowIndex++);
                if (row == null) {
                    continue;
                }
                
                try {
                    UserImportDTO userDTO = new UserImportDTO();
                    
                    // 读取Excel数据
                    userDTO.setUsername(getCellValueAsString(row.getCell(0)));
                    userDTO.setPassword(getCellValueAsString(row.getCell(1)));
                    userDTO.setRealName(getCellValueAsString(row.getCell(2)));
                    userDTO.setEmail(getCellValueAsString(row.getCell(3)));
                    userDTO.setTel(getCellValueAsString(row.getCell(4)));
                    userDTO.setDeptName(getCellValueAsString(row.getCell(5)));
                    
                    // 设置默认状态为正常
                    userDTO.setStatus(1);
                    
                    // 验证必填字段
                    if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
                        throw new IllegalArgumentException("用户名不能为空");
                    }
                    if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                        throw new IllegalArgumentException("密码不能为空");
                    }
                    if (userDTO.getRealName() == null || userDTO.getRealName().isEmpty()) {
                        throw new IllegalArgumentException("用户昵称不能为空");
                    }
                    
                    // 检查用户名是否已存在
                    if (userMapper.selectUserByUsername(userDTO.getUsername())) {
                        throw new IllegalArgumentException("用户名已存在: " + userDTO.getUsername());
                    }
                    
                    // 根据部门名称查找部门ID
                    Long deptId = 0L;
                    if (userDTO.getDeptName() != null && !userDTO.getDeptName().isEmpty()) {
                        Dept dept = deptMapper.selectByDeptName(userDTO.getDeptName());
                        if (dept != null) {
                            deptId = dept.getDeptId();
                        } else {
                            throw new IllegalArgumentException("部门不存在: " + userDTO.getDeptName());
                        }
                    }
                    userDTO.setDeptId(deptId);
                    
                    // 转换为用户实体并保存
                    User user = new User();
                    BeanUtils.copyProperties(userDTO, user);
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    user.setCreateTime(LocalDateTime.now());
                    
                    if (userMapper.insert(user) == 1) {
                        successCount++;
                    } else {
                        failCount++;
                        errorMessages.add("第" + rowIndex + "行: 保存失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("第" + rowIndex + "行: " + e.getMessage());
                }
            }
            
            workbook.close();
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);
        
        return result;
    }
    
    @Override
    public void exportUsers(UserQuery userQuery, HttpServletResponse response) throws IOException {
        // 查询用户数据
        Page<UserVO> page = new Page<>(1, Integer.MAX_VALUE);
        Page<UserVO> userPage = userMapper.selectUser(userQuery, page);
        List<UserVO> users = userPage.getRecords();
        
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户数据");
        
        // 创建标题行样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"用户名", "用户昵称", "邮箱", "手机号码", "部门", "状态", "创建时间", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 创建数据行样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        
        // 填充数据
        int rowIndex = 1;
        for (UserVO user : users) {
            Row row = sheet.createRow(rowIndex++);
            
            // 用户名
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getUsername() != null ? user.getUsername() : "");
            cell0.setCellStyle(dataStyle);
            
            // 用户昵称
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getRealName() != null ? user.getRealName() : "");
            cell1.setCellStyle(dataStyle);
            
            // 邮箱
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getEmail() != null ? user.getEmail() : "");
            cell2.setCellStyle(dataStyle);
            
            // 手机号码
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getTel() != null ? user.getTel() : "");
            cell3.setCellStyle(dataStyle);
            
            // 部门
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getDeptName() != null ? user.getDeptName() : "");
            cell4.setCellStyle(dataStyle);
            
            // 状态
            Cell cell5 = row.createCell(5);
            int status = user.getStatus();
            String statusText = status == 1 ? "正常" : (status == 3 ? "禁用" : "失效");
            cell5.setCellValue(statusText);
            cell5.setCellStyle(dataStyle);
            
            // 创建时间
            Cell cell6 = row.createCell(6);
            if (user.getCreateTime() != null) {
                cell6.setCellValue(user.getCreateTime().toString().replace('T', ' '));
            } else {
                cell6.setCellValue("");
            }
            cell6.setCellStyle(dataStyle);
            
            // 备注
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(user.getRemark() != null ? user.getRemark() : "");
            cell7.setCellStyle(dataStyle);
        }
        
        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // 设置最小宽度
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
        }
        
        // 设置响应头
        String fileName = URLEncoder.encode("用户数据.xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        
        // 输出Excel文件
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
            os.flush();
        } finally {
            workbook.close();
        }
    }
    
    /**
     * 获取单元格的值并转换为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
