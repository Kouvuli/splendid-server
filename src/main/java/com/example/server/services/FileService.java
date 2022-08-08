package com.example.server.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FileService {

    private final Path storageFolder= Paths.get("uploads");

    public FileService() {
        try{
            Files.createDirectories(storageFolder);
        }catch (IOException e){
            throw new RuntimeException("Cannot initial storage",e);
        }
    }
    private boolean isImageFile(MultipartFile file){
        String fileExtension= FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[]{"png","jpeg","jpg","bmp"}).contains(fileExtension.trim().toLowerCase());
    }
    public String storeFile(MultipartFile file){

        try{
            if(file.isEmpty()){
                throw new RuntimeException("File is empty");
            }
            if(!isImageFile(file)){
                throw new RuntimeException("This not image file");
            }

            float fileSizeInMegabytes= file.getSize()/1_000_000.0f;
            if(fileSizeInMegabytes>5.0f){
                throw new RuntimeException("File must be <=5.0Mb");
            }
            String fileExtension= FilenameUtils.getExtension(file.getOriginalFilename());
            String generateFileName= UUID.randomUUID().toString().replace("-","");
            generateFileName = generateFileName + "." + fileExtension;
            Path destinationFilePath=this.storageFolder.resolve(Paths.get(generateFileName)).normalize().toAbsolutePath();
            if(!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
                throw new RuntimeException("cannot store file outside current directory");
            }

            try(InputStream inputStream=file.getInputStream()){
                Files.copy(inputStream,destinationFilePath, StandardCopyOption.REPLACE_EXISTING);

            }
            return generateFileName;
        }


        catch (IOException e){
            throw new RuntimeException("Failed to store file",e);
        }
    }
    public byte[] readFileNotSaved(MultipartFile file) throws IOException {
        InputStream initialStream = file.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        return buffer;
    }
    public byte[] readFileContent(String fileName){
        try{
            Path file =storageFolder.resolve(fileName);
            Resource resource=new UrlResource(file.toUri());
            if(resource.exists()||resource.isReadable()){
                byte[] bytes= StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            }else{
                throw new RuntimeException("Could not read file "+fileName);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAllFiles(){

    }
}
