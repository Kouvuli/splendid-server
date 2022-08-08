package com.example.server.controllers;

import com.example.server.models.*;
import com.example.server.payloads.response.ResponseObject;
import com.example.server.services.ReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "api/v1/reaction")
public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }


    @GetMapping("")
    ResponseEntity<ResponseObject> getReactionCount(
            @RequestParam(required = false,name = "post_id") Integer postId,
            @RequestParam(required = false,name = "comment_id") Integer commentId
    ){
        if(postId==null &&commentId==null){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","successfully get data",reactionService.getAllReaction())
            );
        }

//        long reactionCount=0;
        List<Reaction> foundReactions=new ArrayList<>();
        if(postId!=null){
            foundReactions=reactionService.getReacionsByPostId(postId);
//            reactionCount=reactionService.getTargetCountByPostId(String.valueOf(postId));
        } else if (commentId!=null) {
            foundReactions=reactionService.getReacionsByCommentId(commentId);
//            reactionCount=reactionService.getTargetCountByCommentId(String.valueOf(commentId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
              new ResponseObject("ok","get reaction succesfully",foundReactions)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getReactionById(@PathVariable int id){
        Optional<Reaction> post=reactionService.getReactionById(id);
        return post.isPresent()?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query reaction succesfully",post)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed","Cannot find reaction with id="+id,"")
                );
    }

    @PostMapping("")
    ResponseEntity<ResponseObject> insertReaction(@RequestBody Reaction newReaction){
        if(newReaction.getPost()!=null){

            newReaction.setPost(reactionService.getPostById(newReaction.getPost().getId()));
        }
        else if(newReaction.getComment()!=null){
            newReaction.setComment(reactionService.getCommentById(newReaction.getComment().getId()));
        }
        newReaction.setAuthor(reactionService.getUserById(newReaction.getAuthor().getId()));
        newReaction.setCreateAt(new Timestamp(System.currentTimeMillis()));

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","Insert reaction succesfully",reactionService.addReaction(newReaction))
        );
    }

    @DeleteMapping("")
    ResponseEntity<ResponseObject> deleteReaction(
            @RequestParam(required = false,name = "author_id") Integer authorId,
            @RequestParam(required = false,name = "post_id") Integer postId,
            @RequestParam(required = false,name = "comment_id") Integer commentId
    ){
//        boolean exists=reactionService.ifReactionExists(id);
        Integer reactionId=null;
        if(postId!=null){

            reactionId=reactionService.getReactionIdByAuthorIdAndPostId(String.valueOf(authorId),String.valueOf(postId));
        }
        else{
            reactionId=reactionService.getReactionIdByAuthorIdAndCommentId(String.valueOf(authorId),String.valueOf(commentId));
        }
        if(reactionId!=null){
            reactionService.deleteReactionById(reactionId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Deleted reaction succesfully","")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed","Cannot find reaction to delete","")
        );
    }
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteReaction(@PathVariable int id){
        boolean exists=reactionService.ifReactionExists(id);
        if(exists){
            reactionService.deleteReactionById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Deleted reaction succesfully","")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed","Cannot find reaction to delete","")
        );
    }
}
