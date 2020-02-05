package com.geoxus.core.ueditor;

import com.geoxus.core.ueditor.define.GXActionMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器
 */
final class GXConfigManager {
    private String configFile;

    private JSONObject jsonConfig = null;

    // 涂鸦上传filename定义
    private static final String SCRAWL_FILE_NAME = "scrawl";

    // 远程图片抓取filename定义
    private static final String REMOTE_FILE_NAME = "remote";

    /**
     * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
     */
    private GXConfigManager(String configFile) throws IOException {
        this.configFile = configFile;
        this.initEnv();
    }

    /**
     * 配置管理器构造工厂
     *
     * @param configFile 配置文件
     * @return 配置管理器实例或者null
     */
    static GXConfigManager getInstance(String configFile) {
        try {
            return new GXConfigManager(configFile);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证配置文件加载是否正确
     */
    boolean valid() {
        return this.jsonConfig != null;
    }

    JSONObject getAllConfig() {
        return this.jsonConfig;
    }

    Map<String, Object> getConfig(int type) throws JSONException {
        Map<String, Object> conf = new HashMap<>();
        String savePath = null;
        switch (type) {
            case GXActionMap.UPLOAD_FILE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("fileMaxSize"));
                conf.put("allowFiles", this.getArray("fileAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("fileFieldName"));
                savePath = this.jsonConfig.getString("filePathFormat");
                break;

            case GXActionMap.UPLOAD_IMAGE:
                conf.put("isBase64", "false");
                conf.put("maxSize", this.jsonConfig.getLong("imageMaxSize"));
                conf.put("allowFiles", this.getArray("imageAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("imageFieldName"));
                savePath = this.jsonConfig.getString("imagePathFormat");
                break;

            case GXActionMap.UPLOAD_VIDEO:
                conf.put("maxSize", this.jsonConfig.getLong("videoMaxSize"));
                conf.put("allowFiles", this.getArray("videoAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("videoFieldName"));
                savePath = this.jsonConfig.getString("videoPathFormat");
                break;

            case GXActionMap.UPLOAD_SCRAWL:
                conf.put("filename", GXConfigManager.SCRAWL_FILE_NAME);
                conf.put("maxSize", this.jsonConfig.getLong("scrawlMaxSize"));
                conf.put("fieldName", this.jsonConfig.getString("scrawlFieldName"));
                conf.put("isBase64", "true");
                savePath = this.jsonConfig.getString("scrawlPathFormat");
                break;

            case GXActionMap.CATCH_IMAGE:
                conf.put("filename", GXConfigManager.REMOTE_FILE_NAME);
                conf.put("filter", this.getArray("catcherLocalDomain"));
                conf.put("maxSize", this.jsonConfig.getLong("catcherMaxSize"));
                conf.put("allowFiles", this.getArray("catcherAllowFiles"));
                conf.put("fieldName", this.jsonConfig.getString("catcherFieldName") + "[]");
                savePath = this.jsonConfig.getString("catcherPathFormat");
                break;

            case GXActionMap.LIST_IMAGE:
                conf.put("allowFiles", this.getArray("imageManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getString("imageManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("imageManagerListSize"));
                break;

            case GXActionMap.LIST_FILE:
                conf.put("allowFiles", this.getArray("fileManagerAllowFiles"));
                conf.put("dir", this.jsonConfig.getString("fileManagerListPath"));
                conf.put("count", this.jsonConfig.getInt("fileManagerListSize"));
                break;
        }
        conf.put("savePath", savePath);
        return conf;
    }

    private void initEnv() throws IOException {
        String configContent = this.readFile(this.configFile);
        try {
            this.jsonConfig = new JSONObject(configContent);
        } catch (Exception e) {
            this.jsonConfig = null;
        }
    }

    private String[] getArray(String key) throws JSONException {
        JSONArray jsonArray = this.jsonConfig.getJSONArray(key);
        String[] result = new String[jsonArray.length()];
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            result[i] = jsonArray.getString(i);
        }
        return result;
    }

    /**
     * 读取配置文件
     *
     * @param path resources目录下配置文件的位置 如（static/ueditor/config.json）
     */
    private String readFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path), "UTF-8");
            BufferedReader bfReader = new BufferedReader(reader);
            String tmpContent = null;
            while ((tmpContent = bfReader.readLine()) != null) {
                builder.append(tmpContent);
            }
            bfReader.close();
        } catch (UnsupportedEncodingException e) {
            // 忽略
        }
        return this.filter(builder.toString());
    }

    /**
     * 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
     */
    private String filter(String input) {
        return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }
}
