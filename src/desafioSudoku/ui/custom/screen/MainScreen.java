package desafioSudoku.ui.custom.screen;

import desafioSudoku.model.Space;
import desafioSudoku.service.BoardService;
import desafioSudoku.service.EventEnum;
import desafioSudoku.service.NotifierService;
import desafioSudoku.ui.custom.button.CheckGameStatusButton;
import desafioSudoku.ui.custom.button.FinishGameButton;
import desafioSudoku.ui.custom.button.ResetButton;
import desafioSudoku.ui.custom.frame.MainFrame;
import desafioSudoku.ui.custom.input.NumberText;
import desafioSudoku.ui.custom.panel.MainPanel;
import desafioSudoku.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static javax.swing.JOptionPane.*;

public class MainScreen {
    private JButton finishGameButton;
    private JButton checkGameStatusButton;
     private JButton  resetButton;
    private final static Dimension dimension = new Dimension(600,600);

    public final BoardService broadService;
    private final NotifierService notifierService;




    public MainScreen(Map<String,String> gameConfig) {
        this.broadService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }
    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int r = 0; r < 9; r+=3) {
            var endRow = r+2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c+2;
                var spaces = getSpacesFromSector(broadService.getSpaces(),c,endCol,r,endRow);
                mainPanel.add(generateSecion(spaces));
        }}
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();

    }

    private void addFinishGameButton(JPanel mainPanel) {
         finishGameButton = new FinishGameButton(e->{
            if(broadService.gameIsFinished()){
                JOptionPane.showMessageDialog(null, "Parabens");
                resetButton.setEnabled(false);
                checkGameStatusButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            }else {
                JOptionPane.showMessageDialog(null, "Seu jogo tem alguma inconsistencia, ajuste e tente novamente");
            }
         });
        mainPanel.add(finishGameButton);

    }

    private void addCheckGameStatusButton(JPanel mainPanel) {
         checkGameStatusButton = new CheckGameStatusButton(e->{
            var hasErrors = broadService.hasErros();
            var gameStatus = broadService.getStatus();
            var message = switch (gameStatus){
                case NON_STARTED -> "O jogo não foi iniciado";
                case COMPLETE -> "O jogo esta completo";
                case INCOMPLETE ->"O jogo esta incompleto";
            };
            message += hasErrors? " e contem erros": " e não contem erros";
            JOptionPane.showMessageDialog(null, message);

        });
        mainPanel.add(checkGameStatusButton);

    }

    private void addResetButton(final JPanel mainPanel) {
          resetButton = new ResetButton(e -> {
            var dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if(dialogResult == 0){
                broadService.reset();
                notifierService.notify(EventEnum.CLEAR_SPACE);
            }
        });
        mainPanel.add(resetButton);
    }
    private JPanel generateSecion(List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t->notifierService.subscribe(EventEnum.CLEAR_SPACE,t));
        return new SudokuSector(fields);
    }
    private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
                                            final int initCol, final int endCol,
                                            final int initRow,final int endRow){
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <=endRow ; r++) {
            for (int c = initCol; c <= endCol ; c++) {
                spaceSector.add(spaces.get(c).get(r));

            }
            
        }
        return spaceSector;

    }
}
