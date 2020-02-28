package cn.yh.yfs.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 视频处理工具类
 *
 * @author yanghua
 */
@Slf4j
public class VideoUtils {
    /**
     * 图片格式定义
     */
    private static final String IMAGEMAT = "jpg";

    /**
     * 从视频文件中提取第一帧转为图片作为缩略图
     *
     * @param videoFilePathName
     * @param pictureFolderPath
     * @throws Exception
     */
    public static File fetchFrame(String videoFilePathName, String pictureFolderPath) throws IOException {
        log.info("开始从视频文件：{}中提取帧，作为图片文件存储至：{}", videoFilePathName, pictureFolderPath);
        long start = System.currentTimeMillis();

        File videoFile = new File(videoFilePathName);
        if (videoFile.exists()) {
            // 若不存在则创建图片文件夹
            File folder = new File(pictureFolderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String originalFullFileName = videoFile.getName();
            String picName = originalFullFileName.substring(0, originalFullFileName.lastIndexOf("."));
            String picFullPathName = pictureFolderPath + "/" + picName + "." + IMAGEMAT;
            File pictureFile = new File(picFullPathName);
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoFile);
            fFmpegFrameGrabber.start();
            Frame frame = fFmpegFrameGrabber.grabImage();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.getBufferedImage(frame);
            ImageIO.write(bufferedImage, IMAGEMAT, pictureFile);
            fFmpegFrameGrabber.stop();
            log.info("耗时：{} ms，提取视频帧图片{}完成", System.currentTimeMillis() - start, pictureFile.getAbsolutePath());
            return pictureFile;
        } else {
            log.error("文件：{}不存在！", videoFilePathName);
            return null;
        }
    }

    public static File fetchFrame(String videoFilePathName) throws IOException {
        // 将视频文件地址所在目录截取出来，再加上"/frame"作为缩略图存储路径
        String videoFolderPath = videoFilePathName.substring(0, videoFilePathName.lastIndexOf(File.separator));
        return fetchFrame(videoFilePathName, videoFolderPath + "/frame");
    }
}
