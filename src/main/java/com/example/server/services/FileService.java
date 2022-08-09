package com.example.server.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("splendid-4d803.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("./splendid-4d803-firebase-adminsdk-j6aw3-0f31c4f999.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        String a=URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return String.format("https://firebasestorage.googleapis.com/v0/b/splendid-4d803.appspot.com/o/%s?alt=media", URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    public String upload(MultipartFile multipartFile) {

        try {
            if(multipartFile.isEmpty()){
                throw new RuntimeException("File is empty");
            }
            if(!isImageFile(multipartFile)){
                throw new RuntimeException("This not image file");
            }

            float fileSizeInMegabytes= multipartFile.getSize()/1_000_000.0f;
            if(fileSizeInMegabytes>5.0f){
                throw new RuntimeException("File must be <=5.0Mb");
            }
            String fileName = multipartFile.getOriginalFilename();                        // to get original file name
            fileName = UUID.randomUUID().toString().concat("."+FilenameUtils.getExtension(fileName));  // to generated random string values for file name.

            File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
            String TEMP_URL = this.uploadFile(file, fileName);                                   // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            return TEMP_URL;                     // Your customized response
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
