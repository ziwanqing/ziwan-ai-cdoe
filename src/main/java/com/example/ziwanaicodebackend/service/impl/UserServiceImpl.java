package com.example.ziwanaicodebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.ziwanaicodebackend.exception.BusinessException;
import com.example.ziwanaicodebackend.exception.ErrorCode;
import com.example.ziwanaicodebackend.model.dto.user.UserQueryRequest;
import com.example.ziwanaicodebackend.model.enums.UserRoleEnum;
import com.example.ziwanaicodebackend.model.vo.LoginUserVO;
import com.example.ziwanaicodebackend.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.ziwanaicodebackend.model.entity.User;
import com.example.ziwanaicodebackend.mapper.UserMapper;
import com.example.ziwanaicodebackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.ziwanaicodebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/ziwanqing">紫菀</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码长度过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "两次输入的密码不一致");
        }

        // 2. 查询用户是否存在
        User oldUser = userMapper.selectByAccount(userAccount);
        if (oldUser != null) {
            if (oldUser.getIsDelete() == 0) {
                // 已存在且未删除 → 禁止注册
                throw new BusinessException(ErrorCode.PARAM_ERROR, "账号已存在");
            } else {


                // 已存在但逻辑删除 → 恢复
                oldUser.setIsDelete(0);
                oldUser.setUserPassword(getEncryptPassword(userPassword));
                int restored = userMapper.restoreUser(oldUser.getId(), oldUser.getUserPassword());
                if (restored <= 0) {
                    log.info("恢复用户失败: {}", userAccount);
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "注册失败，数据库错误");
                }
                log.info("用户恢复成功: {}", userAccount);
                return oldUser.getId(); // 恢复成功
            }
        }

        // 3. 加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 创建用户，插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userAccount);
        user.setUserAvatar("https://turbo.6tu.com/2025/03/20250312113510763-400x400.jpg");
        user.setUserRole(UserRoleEnum.USER.getRole());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码长度过短");
        }
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在或密码错误");
        }
        // 4. 如果用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5. 返回脱敏的用户信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询当前用户信息
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id) // where id = ${id}
                .eq("userRole", userRole) // and userRole = ${userRole}
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "ziwan";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
