package com.aimons.jelivebien.controller;

import com.aimons.jelivebien.model.FilePost;
import com.aimons.jelivebien.repository.FilePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FilePostController {

    @Autowired
    private FilePostRepository filePostRepository;

    @PostMapping("/files")
    public String uploadMultipartFile(@RequestParam("files") MultipartFile[] files, Model modal) {
        try {
            // Declare empty list for collect the files data
            // which will come from UI
            List<FilePost> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String fileContentType = file.getContentType();
                Long fileSize = file.getSize();
                byte[] sourceFileContent = file.getBytes();

                FilePost file_post = new FilePost(fileName,fileContentType,fileSize,sourceFileContent);

                // Adding file into fileList
                fileList.add(file_post);
            }

            // Saving all the list item into database
            for (FilePost fileModal : fileList)
                filePostRepository.save(fileModal);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send file list to View using modal class
        // fileServiceImplementation.getAllFiles() used to
        // fetch all file list from DB
        modal.addAttribute("allFiles", filePostRepository.findAll());

        return "FileList";
    }
}
