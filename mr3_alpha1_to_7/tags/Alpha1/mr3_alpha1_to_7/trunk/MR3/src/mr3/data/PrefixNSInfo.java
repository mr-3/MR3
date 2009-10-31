package mr3.data;
/**
 * PrefixNSSet
 *
 * �N���X�����킩��ɂ����̂ŁC�����Ƃ킩��₷�����O�ɕύX�������D
 * ���O��Ԃ�ړ����Œu�������邩�ǂ��������߂鎞�Ɏg�� 
 * isAvailable��true�Ȃ�u��������D
 *
 */
public class PrefixNSInfo {

    private String prefix;
    private String nameSpace;
    private boolean isAvailable;
    
    public PrefixNSInfo(String p, String ns, boolean t) {
        prefix = p;
        nameSpace = ns;
        isAvailable = t;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

	public String toString() {
		return "prefix: "+prefix+" | NameSpace: "+nameSpace+" | available: "+isAvailable;	
	}
}
