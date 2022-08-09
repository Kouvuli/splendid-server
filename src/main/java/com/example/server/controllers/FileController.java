package com.example.server.controllers;


import com.example.server.payloads.response.ResponseObject;
import com.example.server.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins="*",maxAge = 3600)
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
//    @PostMapping("")
//    public ResponseEntity<byte[]> uploadFileNotSaved(@RequestParam("file")MultipartFile file){
//        try{
//            byte[]result=fileService.readFileNotSaved(file);
//
//            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(result);
//        }catch(Exception e){
//            return ResponseEntity.noContent().build();
//        }
//    }
    @PostMapping("")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam("file") MultipartFile file){
        try{
            String link= fileService.upload(file);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok","upload successfully",  link));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ResponseObject("failed",e.getMessage(),""));
        }
    }

//    @GetMapping("/uploads/{fileName:.+}")
//    public ResponseEntity<byte[]> readFile(@PathVariable String fileName){
//        try{
//            byte[] bytes=fileService.readFileContent(fileName);
//            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(bytes);
//        }
//        catch (Exception e){
//            return ResponseEntity.noContent().build();
//        }
//    }
}
