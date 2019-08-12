package pl.dev.news.devnewsservice.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.exception.ExceptionsConstants.userNotFound;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional
    public void delete(final UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(userNotFound);
        }
        userRepository.softDelete(userId);
    }

    @Override
    public RestUserModel get(final UUID userId) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound));
        return UserMapper.INSTANCE.toModel(userEntity);
    }

    @Override
    public Page<RestUserModel> getUsers(
            final String username,
            final String name,
            final String email,
            final Integer page,
            final Integer size
    ) {
        return userRepository
                .findAll(username, name, email, PageRequest.of(page - 1, size))
                .map(userMapper::toModel);
    }

}