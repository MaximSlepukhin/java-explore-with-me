package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение списка пользователей.");
        int page = from / size;
        int offset = from % size;
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsers(ids, pageable, offset, size);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequestDto) {
        log.info("POST запрос на добавление пользователя {}", newUserRequestDto);
        return userService.addUser(newUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE запрос на удаление пользователя с id=" + userId + ".");
        userService.deleteUser(userId);
    }

}
