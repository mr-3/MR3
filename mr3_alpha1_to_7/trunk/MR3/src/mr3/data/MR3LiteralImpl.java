/*
 * Created on 2003/06/08
 *
 */
package mr3.data;

import java.io.*;

/**
 * @author takeshi morita
 * 
 * �v���W�F�N�g��ۑ����邽�߂ɍ�����N���X
 * LiteralImpl�ɑ�������V���A���C�Y�\�ȃN���X��������܂ł́C
 * �ꎞ�I�ɂ��̃N���X���g���āC�V���A���C�Y���邱�Ƃɂ���D
 */
public class MR3LiteralImpl implements Serializable {
	String str;
	String lang;	
	
	MR3LiteralImpl(String s, String l) {
		str = s;
		lang = l;
	}
	
	public String getString() {
		return str;
	}
	
	public String getLanguage() {
		return lang;
	}
}
