package com.geoxus.core.common.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GXUploadUtils {
    /**
     * 单文件上传
     *
     * @param file
     * @param path
     * @return
     * @throws IOException
     */
    public static String singleUpload(MultipartFile file, String path) throws IOException {
        if (!mkdirs(path)) {
            return "";
        }
        String fileName;
        fileName = file.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf(('.')) + 1);
        String newFileName = IdUtil.randomUUID() + "." + suffix;
        byte[] bytes = file.getBytes();
        try (BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(new File(path, newFileName)))) {
            buffStream.write(bytes);
            buffStream.flush();
        }
        return newFileName;
    }

    /**
     * 多文件上传
     *
     * @param files
     * @param path
     * @return
     * @throws IOException
     */
    public static List<Dict> multiUpload(MultipartFile[] files, String path) throws IOException {
        if (!mkdirs(path)) {
            return Collections.emptyList();
        }
        String fileName;
        List<Dict> retList = new ArrayList<>();
        for (MultipartFile file : files) {
            fileName = singleUpload(file, path);
            final Dict dict = Dict.create()
                    .set("size", file.getSize())
                    .set("file_name", fileName)
                    .set("disk", path)
                    .set("mime_type", file.getContentType())
                    .set("name", file.getOriginalFilename())
                    .set("file_path", fileName)
                    .set("collection_name", "default");
            retList.add(dict);
        }
        return retList;
    }

    /**
     * 创建上传目录
     *
     * @param path
     * @return
     */
    private static boolean mkdirs(String path) {
        File dirFile = new File(path);
        if (!dirFile.exists() && !dirFile.isDirectory()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    /**
     * Base64文件上传
     *
     * @param base64 文件base64编码信息
     * @param path   文件存放路径
     * @return
     * @throws IOException
     */
    public static String base64Upload(String base64, String path) throws IOException {
        if (!mkdirs(path)) {
            return "";
        }
        String imgData = base64.substring(base64.indexOf(";base64,") + ";base64,".length());
        byte[] bytes = Base64.decodeBase64(imgData);
        String suffix = base64.substring(0, base64.indexOf(";base64,") + ";base64,".length())
                .replaceAll("data:image/", "").replaceAll(";base64,", "");
        String fileName = IdUtil.randomUUID() + "." + suffix;
        File file = new File(path, fileName);
        FileUtils.writeByteArrayToFile(file, bytes);
        return fileName;
    }
}
