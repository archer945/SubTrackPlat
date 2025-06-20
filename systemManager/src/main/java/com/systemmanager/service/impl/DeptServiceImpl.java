package com.systemmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemmanager.entity.Dept;
import com.systemmanager.mapper.DeptMapper;
import com.systemmanager.service.IDeptService;
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
