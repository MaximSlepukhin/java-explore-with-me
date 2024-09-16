package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable, Integer offset, Integer size) {
        List<User> listOfUsers;
        if (ids == null) {
            listOfUsers = userRepository.findAll(pageable).getContent();
        } else {
            listOfUsers = userRepository.findUserByIdIn(ids, pageable);
        }
        List<UserDto> listOfUsersDto = listOfUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        if (listOfUsersDto.size() > offset) {
            return listOfUsersDto.subList(offset, Math.min(offset + size, listOfUsersDto.size()));
        } else {
            return List.of();
        }
    }

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
