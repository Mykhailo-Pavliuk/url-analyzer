package com.mykhailopavliuk.service.impl;

import com.mykhailopavliuk.exception.NullEntityReferenceException;
import com.mykhailopavliuk.model.User;
import com.mykhailopavliuk.repository.UserRepository;
import com.mykhailopavliuk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if (user != null) {
            return userRepository.save(user);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Override
    public User readById(Long id) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
