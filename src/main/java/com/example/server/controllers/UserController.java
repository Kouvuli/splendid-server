package com.example.server.controllers;

import com.example.server.models.Pagination;
import com.example.server.models.User;
import com.example.server.payloads.response.ResponseObject;
import com.example.server.payloads.response.ResponseObjectPagination;
import com.example.server.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    ResponseEntity<ResponseObjectPagination> getAllUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int limit
    ){
        if(page<0 || limit <1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectPagination(new Pagination(),"failed","Cannot find user","")
            );
        }
        Page<User> userPage=userService.getUser(page,limit);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectPagination(new Pagination(userPage.getTotalPages()-1,userPage.hasNext(),page,limit),"ok","",userPage.getContent())
        );
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getUserById(@PathVariable int id){
        Optional<User> user=userService.getUserById(id);
        return user.isPresent()?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query reaction succesfully",user)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Cannot find reaction with id="+id,"")
                );
    }


    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateUser(@RequestBody User newUser, @PathVariable int id){
        User updatedUser= userService.getUserById(id)
                .map(user->{
                    user.setAddress(newUser.getAddress());
                    user.setDob(newUser.getDob());
                    user.setJob(newUser.getJob());
                    user.setFullname(newUser.getFullname());
                    user.setAvatar(newUser.getAvatar());
                    user.setBackground(newUser.getBackground());

                    return userService.addUser(user);
                }).orElseGet(()->{
                    newUser.setId(id);
                    return userService.addUser(newUser);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","Update User successfully",userService.addUser(updatedUser))
        );
    }
}
