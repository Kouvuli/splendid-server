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
                    if(!newUser.getAddress().isEmpty())
                        user.setAddress(newUser.getAddress());
                    if(!newUser.getDob().isEmpty()){

                        user.setDob(newUser.getDob());
                    }
                    if(!newUser.getJob().isEmpty()){

                        user.setJob(newUser.getJob());
                    }
                    if(!newUser.getFullname().isEmpty()){

                        user.setFullname(newUser.getFullname());
                    }
                    if(!newUser.getAvatar().isEmpty()){

                        user.setAvatar(newUser.getAvatar());
                    }
                    if(!newUser.getBackground().isEmpty()){

                        user.setBackground(newUser.getBackground());
                    }

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
