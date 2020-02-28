package cn.yh.yfs.controller;

import cn.yh.yfs.util.VideoUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传控制器
 * 实现单文件上传、多文件上传、文件获取
 *
 * @author yanghua
 */
@RestController
public class LocalFileServerController {
    @Value("${files.upload.path}")
    private String filesUploadPath;

    /**
     * 上传路径下的根文件夹，也是通过tomcat服务访问静态资源的路径
     */
    private String folderName="/files";

    SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/");

    /**
     * 单文件上传
     *
     * @param file
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/file")
    public Map<String, String> file(MultipartFile file, HttpServletRequest req) throws Exception {
        return saveFile(file, req);
    }

    /**
     * 多文件上传
     *
     * @param files
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/files")
    public List<Map<String, String>> files(MultipartFile[] files, HttpServletRequest req) throws Exception {
        List<Map<String, String>> res = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            Map<String, String> map = saveFile(file, req);
            res.add(map);
        }
        return res;
    }

    /**
     * 文件路径存储对象
     */
    @Data
    @AllArgsConstructor
    private class Paths {
        /**
         * 按日期分层的文件夹路径
         */
        String dailyPath;
        /**
         * 完整文件存储物理路径
         */
        String realPath;
    }

    private Paths getFilesPath(HttpServletRequest req) {
        String dailyPath = sdf.format(new Date());

        if (StringUtils.isEmpty(filesUploadPath)) {
            String path = req.getServletContext().getRealPath(folderName) + dailyPath;
            return new Paths(dailyPath, path);
        } else {
            String path = filesUploadPath + folderName + dailyPath;
            return new Paths(dailyPath, path);
        }
    }

    /**
     * 文件处理逻辑
     * 注意：仅做了简单文件接收，存储于临时目录
     *
     * @param file
     * @param req
     * @return
     */
    private Map<String, String> saveFile(MultipartFile file, HttpServletRequest req) throws IOException {
        Paths paths = getFilesPath(req);
        File folder = new File(paths.getRealPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String oldName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String uid = UUID.randomUUID().toString();
        String newName = uid + oldName.substring(oldName.lastIndexOf("."));

        try {
            file.transferTo(new File(folder, newName));
            String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + folderName;
            String fileUrl = baseUrl + paths.getDailyPath() + newName;

            Map<String, String> res = new HashMap<>(3);

            // 处理缩略图
            if ("video/mp4".equals(contentType)) {
                File frameFile = VideoUtils.fetchFrame(paths.getRealPath() + newName);
                String frameUrl = baseUrl + paths.getDailyPath() + "frame/" + frameFile.getName();
                res.put("frameUrl", frameUrl);
            }
            res.put("contentType", contentType);
            res.put("originalFilename", oldName);
            res.put("fileUrl", fileUrl);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
