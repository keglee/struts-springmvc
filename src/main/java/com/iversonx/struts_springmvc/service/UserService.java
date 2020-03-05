package com.iversonx.struts_springmvc.service;

import com.iversonx.struts_springmvc.bean.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/9 9:37
 */
@Service
public class UserService {
    private Map<Integer, User> userMap = new HashMap<>(10);
    public UserService() {
        for(int i = 0; i < 10; i++) {
            User u = new User();
            u.setId(buildId());
            u.setUsername("user" + u.getId());
            u.setPassword("123456");
            if(u.getId() % 2 == 0) {
                u.setSex("男");
            } else {
                u.setSex("女");
            }
            userMap.put(u.getId(), u);
        }
    }
    private Integer maxId = 0;
    public List<User> list() {
        List<User> users = new ArrayList<>(userMap.size());
        for(Map.Entry<Integer, User> entry : userMap.entrySet()) {
            users.add(entry.getValue());
        }
        return users;
    }

    /**
     * 详情
     */
    public User detail(Integer id) {
        return userMap.get(id);
    }

    public void add(User user) {
        user.setId(maxId++);
        userMap.put(user.getId(), user);
    }

    public void update(User user) {
        userMap.put(user.getId(), user);
    }

    public void delete(Integer id) {
        userMap.remove(id);
    }

    private Integer buildId() {
        return ++maxId;
    }
}
