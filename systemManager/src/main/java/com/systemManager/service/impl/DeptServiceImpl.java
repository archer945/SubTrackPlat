package com.systemManager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemManager.entity.Dept;
import com.systemManager.mapper.DeptMapper;
import com.systemManager.service.IDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

}
