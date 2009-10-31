package mr3.data;
/**
 * PrefixNSSet
 *
 * クラス名がわかりにくいので，もっとわかりやすい名前に変更したい．
 * 名前空間を接頭辞で置き換えるかどうかを決める時に使う 
 * isAvailableがtrueなら置き換える．
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
