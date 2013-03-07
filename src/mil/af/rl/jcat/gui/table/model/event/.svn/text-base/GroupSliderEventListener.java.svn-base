package mil.af.rl.jcat.gui.table.model.event;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
/**
 * <p>Title: GroupSliderEventListener.java</p>
 * <p>Description: Listens for slider events from the CauseTableModel
 *    and sets it's value in the cell above it in Float (0.0 - 1.0)</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: C3I Associates</p>
 * @author Edward Verenich (CAT R&D Team)
 * @version 1.0
 */

public class GroupSliderEventListener implements ChangeListener {
  private AbstractTableModel model;
  private int row;
  private int col;
  /**
   * Creates a new Group slider Event listener and tells us where
   * it lives (coordinates)
   * @param m CauseTableModel
   * @param row int
   * @param col int
   */
  public GroupSliderEventListener(AbstractTableModel m, int r, int c) {
    model = m;
    row = r;
    col = c;
  }
  public void stateChanged(ChangeEvent e) {
    JSlider s = (JSlider)e.getSource();
    int v = s.getValue();
    model.setValueAt(new Float(v / 100.0f),row - 1, col);
  }


}
