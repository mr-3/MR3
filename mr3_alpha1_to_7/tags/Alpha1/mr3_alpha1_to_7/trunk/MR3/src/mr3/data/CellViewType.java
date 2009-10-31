package mr3.data;
/**
 * Cell��URI�ŕ\�����邩�C���x���ŕ\�����邩�����߂�D
 *
 */
public class CellViewType {
	private String type;
    
	private CellViewType(String t) {
		type = t;
	}

	public static final CellViewType URI = new CellViewType("URI");
	public static final CellViewType LABEL = new CellViewType("Label");
	
	public String toString() {
		return type;
	}
}