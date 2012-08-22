/*
 * 数式展開システム.
 */
package jp.ac.ritsumei.is.hpcss.cellMLonGPU.app;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * ファイル拡張子フィルタクラス.
 */
public class FileExtentionFilter extends FileFilter {

	private String extention = "";
	private String description = "";

	/* (非 Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
        // ディレクトリは常に表示する
        if (f.isDirectory()) {
            return true;
        }
        // ファイルは拡張子が定義されたものだけを表示する
        String fileName = f.getName();
        int extIndex = fileName.lastIndexOf(".");
        if (extIndex >= 0) {
            if (fileName.substring(extIndex + 1).toLowerCase().equals(extention)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 受け付けるファイル拡張子を設定する.
     * @param extention ファイル拡張子
     */
    public void addExtension(String extention) {
    	this.extention = extention.toLowerCase();
    }

    /* (非 Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * 受け付けるファイルの説明文を設定する.
     * @param description 受け付けるファイルの説明文
     */
    public void setDescription(String description) {
    	this.description = description;
    }

}
