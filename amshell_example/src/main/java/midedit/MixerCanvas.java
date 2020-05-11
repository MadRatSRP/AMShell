package midedit;

import midedit.media.Composition;
import java.util.*;
import java.io.*;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.Key;
import main.L;
import main.P;
import main.Main;
import main.Rms;
import ui.TextView;

/**
 *
 * @author user
 */
public abstract class MixerCanvas extends Canvas implements Runnable {

    private static final Font defFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    protected Composition composition;
    protected int channel;
    private Options options;
    private boolean isOptionsView = false,
            isMarkMode = false,
            isMoveMode = false,
            isModeDesc = false,
            isNumsControl = Rms.numKeysEdit;
    private Status status;
    private Main control;
    private MixerModel model;
    private Display display = Main.dsp;
    private Thread playingThread;
    private final Runnable playingRunnable = new Runnable() {

        public void run() {
            while (CompositionForm.isPlaying) {
                try {
                    doSmallRight();
                    Thread.sleep(52);//31);
                } catch (InterruptedException ex) {
                    System.out.println("ex = " + ex);
                }
            }
        }
    };

    /**
     *
     * @param ctrl
     * @param c 
     * @param ch
     */
    public MixerCanvas(Main ctrl, Composition c, int ch) {
        setFullScreenMode(true);
        control = ctrl;
        composition = c;
        channel = ch;
        wOne = Rms.noteWidth;
        hOne = Rms.noteHeight;
        screenHeight = getHeight();
        screenWidth = getWidth();
        statusBegY = screenHeight - statusHeight;
        nW = (screenWidth - wBeg - 5) / wOne;
        nH = (screenHeight - statusHeight - 12) / hOne;
        rollWidth = (nW * wOne + 1);
        rollHeight = (nH * hOne + 1);
        model = control.getModel();
        runningStatus = true;
        options = new Options();
        status = new Status();
        viewModeDesc(L.str[L.editMode]);
        display.callSerially(this);
    }

    /**
     *
     * @param ch
     * @return
     */
    abstract protected int getNoteFromLine(int ch);

    /**
     *
     * @param n 
     * @return
     */
    abstract protected byte getLineFromNote(int n);

    /**
     *
     * @param keyCode
     */
    public void keyPressed(int keyCode) {
        keyCodePressed = keyCode;
        if (isOptionsView) {
            options.keyPressed(keyCodePressed);
            return;
        }
        if (keyPressedCount == 0) {
            keyPressedCount = 1;
        }
        int ga = getGameAction(keyCodePressed);
        if (ga == FIRE) {
            doKEY_NUM5();
            needPaint = true;
            needPaintStatus = true;
            return;
        } else if (ga == LEFT || keyCodePressed == KEY_NUM4) {
            if (isNumsControl ^ (keyCodePressed == KEY_NUM4)) {
                doBigLeft();
            } else {
                doSmallLeft();
            }
            status.resetTimeTune();
            return;
        } else if (ga == RIGHT || keyCodePressed == KEY_NUM6) {
            if (isNumsControl ^ (keyCodePressed == KEY_NUM6)) {
                doBigRight();
            } else {
                doSmallRight();
            }
            status.resetTimeTune();
            return;
        } else if (ga == UP || keyCodePressed == KEY_NUM2) {
            if (isNumsControl ^ (keyCodePressed == KEY_NUM2)) {
                doBigUp();
            } else {
                doSmallUp();
            }
            status.resetTimeTune();
            return;
        } else if (ga == DOWN || keyCodePressed == KEY_NUM8) {
            if (isNumsControl ^ (keyCodePressed == KEY_NUM8)) {
                doBigDown();
            } else {
                doSmallDown();
            }
            status.resetTimeTune();
            return;
        }
        if(keyCodePressed == KEY_NUM7 || keyCodePressed == Key.Call) {
            status.nextView();
            needPaintStatus = true;
        } else if(keyCodePressed == KEY_NUM9 || keyCodePressed == Key.Clear) {
            doKEY_NUM9();
            needPaint = true;
            needPaintStatus = true;
        } if(keyCodePressed == Key.leftSoftKey) {
            isOptionsView = true;
            options.resetMenu();
        } if(keyCodePressed == Key.rightSoftKey) {
            Main.midlet.compositionForm.setComposForm();
        } if(keyCodePressed == Key.Back) {
            composition.getUndoableAction().undo();
            needPaintStatus = true;
            needPaint = true;
            
        } if(keyCodePressed == Key.VolPlus) {
            status.volumePlus();
            needPaintStatus = true;
        } if(keyCodePressed == Key.VolMinus) {
            status.volumeMinus();
            needPaintStatus = true;
        } else switch (keyCodePressed) {
            case KEY_NUM1:
                playFromCurrentPosition();
                break;
            case KEY_NUM3:
                model.playNote(
                        (byte) ((channel == Constants.DRUMS_CHANNEL) ? -10 : composition.getInstrument(channel)),
                        (byte) getNoteFromLine(curY)
                        );
                break;
            case KEY_NUM0:
                status.action();
                needPaintStatus = true;
                break;
            case KEY_STAR:
                status.curParamMinus();
                needPaintStatus = true;
                break;
            case KEY_POUND:
                status.curParamPlus();
                needPaintStatus = true;
                break;
        }
    }

    private void doSmallLeft() {
        if (curX > 0) {
            curX--;
        } else {
            if (xBase >= wStep) {
                xBase -= wStep;
                curX += wStep - 1;
            } else if (xBase > 0) {
                curX += xBase - 1;
                xBase = 0;
            }
            needPaint = true;
            needPaintStatus = true;
        }
    }
    
    private void doSmallRight() {
        if (curX < nW - 1) {
            curX++;
        } else {
            xBase += wStep;
            curX -= wStep - 1;
            needPaint = true;
            needPaintStatus = true;
        }
    }

    private void doSmallUp() {
        if (curY > 0) {
            curY--;
        } else if (hBase >= hStep + hMin) {
            hBase -= hStep;
            curY += hStep - 1;
            needPaint = true;
            needPaintStatus = true;
        }
    }
    
    public void pointerPressed(int x, int y) {
        if(isOptionsView) {
            options.pointerPressed(x, y);
            return;
        }
        boolean needDoKey5 = true;
        curX = ( (x - wBeg) / wOne);
        if(curX >= (nW - nW / 3)) {
            curX -= 2*wStep;
            xBase += 2*wStep;
            needDoKey5 = false;
        } else if(curX < wBeg) {
            curX += 2*wStep;
            xBase -= 2*wStep;
            needDoKey5 = false;
        }
        curY = ( (y - hBeg) / hOne);
        if(needDoKey5) doKEY_NUM5();
        needPaint = true;
        needPaintStatus = true;
    }
    
    public void pointerDragged(int x, int y) {
        isOptionsView = true;
        options.resetMenu();
    }

    protected void doSmallDown() {
        if (curY < nH - 1) {
            curY++;
        } else if (hBase + curY < hMax) {
            hBase += hStep;
            curY -= hStep - 1;
            needPaint = true;
            needPaintStatus = true;
        }
    }

   private void doBigLeft() {
        wStepMeasure = (composition.getNom() * (32 >> composition.getDenomE()));
        if (xBase >= wStepMeasure) {
            xBase -= wStepMeasure;
            needPaint = true;
            needPaintStatus = true;
        }
    }

    private void doBigRight() {
        wStepMeasure = (composition.getNom() * (32 >> composition.getDenomE()));
        xBase += wStepMeasure;
        needPaint = true;
        needPaintStatus = true;
    }

    private void doBigUp() {
        if (curY >= hStep) {
            curY -= hStep;
        } else if (hBase >= hMin + hStep) {
            hBase -= hStep;
            needPaint = true;
        }
        needPaint = true;
        needPaintStatus = true;
    }

    ;

    private void doBigDown() {
        if (curY < nH - hStep) {
            curY += hStep;
        } else if (hBase + curY < hMax - hStep) {
            hBase += hStep;
            needPaint = true;
        }
        needPaint = true;
        needPaintStatus = true;
    }

    ;

    /**
     *
     * @return
     */
    protected boolean doKEY_NUM5() {
        if (isMoveMode == false && isMarkMode == false && curNote != null) {
            timeMarkBeg = getCurTime();
            timeMarkEnd = getCurTime();
            setMarkModeOn();
        }

        if (isMoveMode) {
            int noteShift = getCurNote() - OrigNote;
            if (this instanceof DrumsCanvas) {
                noteShift = -noteShift;
            }
            NoteListUtils.doMove(composition, (byte) channel,
                    getCurTime() - OrigTime,
                    noteShift, status.getVolTune());
            isMoveMode = false;
            setMarkModeOff();
            return true;
        } else if (isMarkMode) {
            if (curNote != null) {

                if (curNote.noteLength > 0) {
                    Note noteTmp;
                    for (noteTmp = curNote.next; noteTmp != null
                            && (noteTmp.noteChannel != curNote.noteChannel || noteTmp.noteLine != curNote.noteLine || noteTmp.noteVolume != 0);
                            noteTmp = noteTmp.next);

                    if (noteTmp != null && noteTmp.noteChannel == note.noteChannel && noteTmp.noteLine == note.noteLine) {
                        noteTmp.mark = (byte) (1 - curNote.mark);
                    }
                }

                curNote.mark = (byte) (1 - curNote.mark);

            } else {
                setMarkModeOff();
            }
            timeMarkBeg = NOT_MARKED;
            timeMarkEnd = NOT_MARKED;
            return true;
        }

        return false;
    }

    /**
     *
     */
    private void doKEY_NUM9() {
        if (isMarkMode) {
            setMarkModeOff();
            return;
        }

        if (curNote != null) {
            composition.delNote(curNote.noteTime, curNote.noteChannel, curNote.noteLine, curNote.noteVolume);
        } else {
            composition.delNote(getCurTime(), (byte) channel, (byte) getNoteFromLine(curY), (byte) 127);
        }
    }

    private void playFromCurrentPosition() {
        try {
            if (CompositionForm.isPlaying) {
                model.stopPlay();
                playingThread.join();
            } else {
                model.playMix(composition, xBase * Constants.timeConst);
                playingThread = new Thread(playingRunnable);
                playingThread.start();
                //System.out.println("CompositionForm.isPlaying = " + CompositionForm.isPlaying);
            }
        } catch (Exception e) {
            System.out.println("Exception = " + e);
        }

    }

    /**
     *
     * @param keyCode
     */
    public void keyReleased(int keyCode) {
        if (keyPressedCount > 0) {
            int gameAction = getGameAction(keyCodePressed);
            if (keyCodePressed == KEY_NUM4 || keyCodePressed == KEY_NUM6
                    || keyCodePressed == KEY_NUM2 || keyCodePressed == KEY_NUM8
                    || keyCodePressed == KEY_POUND || keyCodePressed == KEY_STAR
                    || gameAction == LEFT || gameAction == RIGHT || gameAction == UP || gameAction == DOWN) {
                setNeedPaint();
            }
        }
        keyPressedCount = 0;
    }

    /**
     *
     * @param g
     */
    public void paint(Graphics g) {
        g.translate(0 - g.getTranslateX(), 0 - g.getTranslateY());
        g.setClip(0, 0, screenWidth, screenHeight);

        if (isOptionsView) {
            options.paint(g);
            runningStatus = false;
            return;
        }

        if (keyPressedCount > 0) {
            if (keyPressedCount < 25) {
                keyPressedCount++;

            } else {
                int gameAction = getGameAction(keyCodePressed);
                if (keyCodePressed == KEY_NUM4 || keyCodePressed == KEY_NUM6
                        || gameAction == LEFT || gameAction == RIGHT || gameAction == UP || gameAction == DOWN
                        || keyCodePressed == KEY_NUM2 || keyCodePressed == KEY_NUM8
                        || keyCodePressed == KEY_POUND || keyCodePressed == KEY_STAR) {
                    isblack = true;


                    keyPressed(keyCodePressed);
                }
            }
        }
        paintUniEditor(g);

    }

    /**
     *
     * @param g
     */
    private void paintUniEditor(Graphics g) {
        g.setColor(224, 192, 224);
        g.setGrayScale(0);
        g.setFont(P.smPlain);
        if (needPaint == true) {
            int tmpN = getNoteFromLine(curY);
            if (tmpN < 0 || tmpN > 127) {
                curY = 4;
            }
            paintRoll(g);
        }
        if (needPaintStatus == true) {
            status.paint(g);
        }
        g.translate(wBeg - g.getTranslateX(), hBeg - g.getTranslateY());
        g.setColor(192, 192, 224);
        xt = curXPrev * wOne;
        yt = curYPrev * hOne + 1;
        paintArrows(g, xt, yt);
        if (isblack) {
            g.setColor(192, 0, 0);
        } else {
            g.setColor(192, 192, 224);
        }
        xt = curX * wOne;
        yt = curY * hOne + 1;
        paintArrows(g, xt, yt);
        curXPrev = curX;
        curYPrev = curY;
        isblack = !isblack;
        if (isModeDesc && needPaint) {
            paintModeDesc(g);
            isModeDesc = false;
        }
        needPaint = false;
        needPaintStatus = false;
    }

    private void paintRoll(Graphics g) {
        g.setColor(192, 192, 224);
        g.fillRect(0, 0, screenWidth, statusBegY);
        g.setColor(0);
        g.drawRect(0, 0, screenWidth - 1, statusBegY);
        paintScale(g);
        g.translate(wBeg, hBeg);
        g.setGrayScale(96);
        for (yt = 0; yt < rollHeight; yt += hOne) {
            g.drawLine(0, yt, rollWidth - 1, yt);
        }
        g.setColor(192, 160, 160);
        yt = curY * hOne + 1;
        g.fillRect(0, yt, rollWidth - 1, hOne - 1);
        g.setGrayScale(96);
        int vLineStep = 32 >> composition.getDenomE();
        if (vLineStep == 0) {
            vLineStep = 1;
        }
        for (xt = (vLineStep - xBase % vLineStep) * wOne; xt < rollWidth; xt += vLineStep * wOne) {
            g.drawLine(xt, 0, xt, rollHeight - 1);
        }
        vLineStep = vLineStep * composition.getNom();
        g.setColor(0);
        for (xt = (vLineStep - xBase % vLineStep) * wOne; xt < rollWidth; xt += vLineStep * wOne) {
            g.drawLine(xt, 0, xt, rollHeight - 1);
        }
        g.setClip(1, -2, rollWidth - 2, rollHeight + 4);
        g.setColor(0);
        tBeg = (xBase - 2 * (1 << Constants.MAX_NOTE_LENGTH)) * Constants.timeConst;
        tMax = (xBase + nW) * Constants.timeConst;
        curTime = getCurTime();
        int curTimePlus = curTime + Constants.timeConst - 1;
        int curN = getNoteFromLine(curY);
        curNote = null;
        g.setGrayScale(0);
        for (note = composition.getFirstNote(tBeg, channel);
                note != null && note.noteTime <= tMax; note = note.next) {
            if (note.noteChannel == channel && note.noteVolume != 0) {
                if (note.noteTime <= curTimePlus && note.noteLine == curN
                        && (curTime < note.noteTime + (note.noteLength == 0 ? 1 : note.noteLength))) {
                    curNote = note;
                    if (!isMarkMode) {
                        g.setColor(150, 0, 0);
                    } else if (curNote.mark == NoteListUtils.NOTE_NOT_MARKED) {
                        g.setColor(128, 0, 0);
                    } else {
                        g.setColor(150, 0, 0);
                    }
                    paintNote(g, note, 0, 0);
                    g.setColor(0);
                } else if (note.mark == NoteListUtils.NOTE_MARKED) {
                    g.setColor(150, 0, 0);
                    paintNote(g, note, 0, 0);
                    g.setColor(0);
                } else {
                    int col = (256 - note.noteVolume);
                    g.setColor(col, 0, 140);
                    paintNote(g, note, 0, 0);
                }
            }
        }
        if (isMoveMode) {
            int shiftTime = getCurTime() - OrigTime;
            int shiftNote = getCurNote() - OrigNote;

            if (this instanceof DrumsCanvas) {
                shiftNote = -shiftNote;
            }

            for (note = composition.getUndoableAction().getDeletedList().getFirst();
                    note != null; note = note.next) {
                if (note.noteChannel == channel && note.noteVolume != 0) {
                    g.setColor(128, 0, 0);
                    paintNote(g, note, shiftTime, shiftNote);
                    g.setColor(0);
                }
            }
        }
        g.setClip(-wBeg, -hBeg, screenWidth, statusBegY);
        g.setColor(192, 160, 160);
        xt = curX * wOne;
        g.drawLine(xt, 0, xt, rollHeight - 1);
        g.setGrayScale(0);
        g.drawRect(0, 0, rollWidth - 1, rollHeight - 1);
        paintMarked(g);
        g.translate(0 - g.getTranslateX(), 0 - g.getTranslateY());
        g.setClip(0, 0, getWidth(), getHeight());
    }

    private void paintMarked(Graphics g) {
        g.setColor(64, 32, 224);

        int n = getX(timeMarkBeg);
        xt = n * wOne;
        if (n >= 0 && n < nW) {
            g.drawLine(xt, -1, xt, rollHeight);
            g.drawLine(xt, -1, xt + wOne, -1);
            g.drawLine(xt, rollHeight, xt + wOne, rollHeight);
        }

        n = getX(timeMarkEnd);
        xt = n * wOne;
        if (n > 0 && n < nW) {
            g.drawLine(xt, -1, xt, rollHeight);
            g.drawLine(xt, -1, xt - wOne, -1);
            g.drawLine(xt, rollHeight, xt - wOne, rollHeight);
        }

    }

    private void paintArrows(Graphics g, int x, int y) {
        yt = -2;
        for (dy = 0; dy < 3; ++dy) {
            g.drawLine(x - dy, yt - dy, x + dy, yt - dy);
        }
        yt = rollHeight + 1;
        for (dy = 0; dy < 3; ++dy) {
            g.drawLine(x - dy, yt + dy, x + dy, yt + dy);
        }
        xt = -13;
        for (dx = 0; dx < 3; ++dx) {
            g.drawLine(xt - dx, y - dx, xt - dx, y + dx);
        }
        xt = rollWidth;
        for (dx = 0; dx < 3; ++dx) {
            g.drawLine(xt + dx, y - dx, xt + dx, y + dx);
        }

    }

    /**
     *
     * @param g
     * @param note
     * @param shiftTime
     * @param shiftNote
     */
    protected void paintNote(Graphics g, Note note, int shiftTime, int shiftNote) {
        xt = getXInPixel(note.noteTime + shiftTime);
        yt = getLineFromNote(note.noteLine + shiftNote);
        if (xt >= 0 && xt < rollWidth) {
            if (yt < 0) {
                yt = -1;
            } else if (yt >= nH) {
                yt = nH;
            }

            g.fillRect(xt + 1, yt * hOne + 1, 2, hOne - 1);
        }
    }

    /**
     *
     * @param g
     */
    protected void paintScale(Graphics g) {
        g.drawLine(4, hBeg, 4, hBeg + rollHeight - 1);
        g.drawLine(wBeg - 2, hBeg, wBeg - 2, hBeg + rollHeight - 1);
        int hhtmp;
        int blackButHeight = hOne + 1;
        int blackButYBeg;
        for (hhBase = hBeg; hhBase < hBeg + rollHeight - 1; hhBase += hOne * 12) {
            g.drawLine(3, hhBase, wBeg - 2, hhBase);
            blackButYBeg = hhBase + 1 - hOne / 2;
            g.fillRect(4, blackButYBeg + hOne, wBeg - 10, blackButHeight);
            g.fillRect(4, blackButYBeg + 3 * hOne, wBeg - 10, blackButHeight);
            g.fillRect(4, blackButYBeg + 5 * hOne, wBeg - 10, blackButHeight);
            g.fillRect(4, blackButYBeg + 8 * hOne, wBeg - 10, blackButHeight);
            g.fillRect(4, blackButYBeg + 10 * hOne, wBeg - 10, blackButHeight);
            hhtmp = hhBase + hOne + 1;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
            hhtmp = hhBase + 3 * hOne + 1;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
            hhtmp = hhBase + 5 * hOne + 1;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
            hhtmp = hhBase + 7 * hOne;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
            hhtmp = hhBase + 8 * hOne + 1;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
            hhtmp = hhBase + 10 * hOne + 1;
            g.drawLine(4, hhtmp, wBeg - 2, hhtmp);
        }
        g.drawLine(3, hhBase, wBeg - 2, hhBase);
    }

    private void paintModeDesc(Graphics g) {
        int DESC_WIDTH = screenWidth;
        int DESC_HEIGHT = statusHeight;
        int DESC_BEGX = (screenWidth - DESC_WIDTH) / 2;
        int DESC_BEGY = statusBegY;

        g.translate(0 - g.getTranslateX(), 0 - g.getTranslateY());
        g.setColor(160, 160, 224);
        g.setClip(DESC_BEGX, DESC_BEGY, DESC_WIDTH, DESC_HEIGHT);
        g.fillRect(DESC_BEGX, DESC_BEGY, DESC_WIDTH, DESC_HEIGHT);
        g.setColor(0);
        g.drawRect(DESC_BEGX, DESC_BEGY, DESC_WIDTH - 1, DESC_HEIGHT - 1);
        int nInd = modeDesc.indexOf('\n');
        int strY;
        if (nInd > 0) {
            strY = DESC_BEGY + (DESC_HEIGHT - 2 * defFont.getBaselinePosition()) / 2;
            if (DESC_HEIGHT < 2 * defFont.getHeight()) {
                modeDesc = modeDesc.replace('\n', ' ');
                strY = DESC_BEGY + (DESC_HEIGHT - defFont.getBaselinePosition()) / 2;
            }
        } else {
            strY = DESC_BEGY + (DESC_HEIGHT - defFont.getBaselinePosition()) / 2;
        }
        g.drawString(modeDesc, DESC_BEGX + DESC_WIDTH / 2,
                strY, Graphics.TOP | Graphics.HCENTER);
    }

    /**
     *
     */
    public void setNeedPaint() {
        needPaint = true;
        needPaintStatus = true;
    }

    /**
     *
     */
    public void run() {
        try {
            if (runningStatus) {
                repaint();
                Thread.sleep(10);
                display.callSerially(this);
            }
        } catch (Exception e) {
        }
    }

    /**
     *
     * @return
     */
    protected int getCurTime() {
        return (xBase + curX) * Constants.timeConst + status.getTimeTune();
    }

    /**
     *
     * @param noteTime
     * @return
     */
    private int getX(int t) {
        return t / Constants.timeConst - xBase;
    }

    /**
     *
     * @param t 
     * @return
     */
    protected int getXInPixel(int t) {
        return t * wOne / Constants.timeConst - xBase * wOne;
    }

    /**
     *
     * @return
     */
    protected int getCurNote() {
        return Constants.INV_CANVAS_CONST - (hBase + curY);
    }

    /**
     *
     * @return
     */
    protected byte getCurVol() {
        return status.getVolTune();
    }

    /**
     *
     * @return
     */
    protected int getCurLen() {
        return status.getLenTune();
    }

    /**
     *
     */
    public void showNotify() {
        setNeedPaint();
        runningStatus = true;
        display.callSerially(this);
    }

    /**
     *
     */
    public void hideNotify() {
        runningStatus = false;
        curNote = null;
    }

    /**
     *
     */
    private void setMarkModeOn() {
        if (timeMarkBeg != NOT_MARKED && timeMarkEnd != NOT_MARKED && timeMarkBeg <= timeMarkEnd) {
            if (isMarkMode) {
                NoteListUtils.unMarkNotes(composition, (byte) channel);
            }
            isMarkMode = true;
            model.getBuffer().copy2Buffer(composition, (byte) channel, timeMarkBeg, timeMarkEnd, true);

            viewModeDesc(L.str[L.markMode]);
        }
        note = composition.getFirstNote(timeMarkEnd, channel);
    }

    /**
     *
     */
    private void setMarkModeOff() {
        NoteListUtils.unMarkNotes(composition, (byte) channel);
        isMarkMode = false;
        timeMarkBeg = NOT_MARKED;
        timeMarkEnd = NOT_MARKED;
        viewModeDesc(L.str[L.editMode]);
    }

    private void viewModeDesc(String desc) {
        isModeDesc = true;
        modeDesc = desc;
    }
    private final static int NOT_MARKED = -1000;
    private int OrigTime,
            OrigNote,
            statusHeight = 21,
            screenHeight,
            screenWidth,
            statusBegY,
            nW,
            keyCodePressed = KEY_NUM1;
    protected int curY,
            hMin,
            hMax = 15,
            hBase,
            hStep = 12,
            wBeg = 16,
            hBeg = 5,
            wOne = Rms.noteWidth,
            hOne = Rms.noteHeight,
            nH,
            rollWidth,
            rollHeight;
    //protected String str = " Hello ";
    static private Image statusImg,
            statusImgSel, statusImgCur, statusImgData, statusImgDataCur,
            imgDot, imgTriplet;
    public static int curX,
            xBase;
    private String modeDesc = "";
    private boolean runningStatus,
            needPaintStatus,
            needPaint,
            isblack;
    private int keyPressedCount,
            curYPrev,
            curXPrev,
            wStep = wOne,//5,
            wStepMeasure = 32,
            xt, yt,
            dx, dy,
            tBeg, tMax,
            curTime,
            hhBase,
            timeMarkBeg = NOT_MARKED,
            timeMarkEnd = NOT_MARKED;
    private Note note,
            curNote,
            noteDelta;

    static {
        try {
            statusImg = Image.createImage("/img/attr.png");
            statusImgSel = Image.createImage("/img/attractive.png");
            statusImgCur = Image.createImage("/img/attrcur.png");
            statusImgDataCur = Image.createImage("/img/attrdatacur.png");
            statusImgData = Image.createImage("/img/attrdata.png");
            imgDot = Image.createImage("/img/dot.png");
            imgTriplet = Image.createImage("/img/triplet.png");
        } catch (IOException ex) {
        }
    }

    private class Status {

        private int statusMode;
        private final static int MODE_NONE = -1,
                MODE_TIME = 0,
                MODE_LEN = 1,
                MODE_NOTE = 2,
                MODE_VOL = 3,
                STATUS_ONE_BUT = 25,
                STATUS_BUT_HEIGHT = 9;
        private int[] params;
        private int[] absParams;
        private int[] deltaParams;
        private int pCount = 4;
        private int indCurNoteLen;
        private byte[] noteLen = {1, 2, 4, 8, 16, 32};
        private int extLen;

        public Status() {
            statusMode = MODE_NONE;
            if (MixerCanvas.this instanceof DrumsCanvas) {
                statusMode = MODE_NOTE;
            }


            absParams = new int[pCount];
            deltaParams = new int[pCount];
            params = absParams;

            params[MODE_TIME] = 0;
            params[MODE_LEN] = 10;
            params[MODE_NOTE] = 0;
            params[MODE_VOL] = 90;

            resetDelta();

            indCurNoteLen = 3;
            extLen = 0;
            calcLen();
        }

        private void resetDelta() {
            deltaParams[MODE_TIME] = 0;
            deltaParams[MODE_LEN] = 0;
            deltaParams[MODE_NOTE] = 0;
            deltaParams[MODE_VOL] = 0;

        }

        private void setAbsParams() {
            params = absParams;
        }

        private void setDeltaParams() {
            params = deltaParams;
        }

        private void nextView() {
            statusMode++;
            if (statusMode >= pCount) {
                statusMode = MODE_NONE;
            }
            if (params == deltaParams) {
                if (!isValidTab()) {
                    nextView();
                }
            }

        }

        private boolean isValidTab() {
            boolean ok = true;
            if (params == deltaParams && (statusMode == 1 || statusMode == 2)) {
                ok = false;
            }
            return ok;
        }

        private void resetTimeTune() {
            params[MODE_TIME] = 0;
        }

        private void curParamPlus() {
            if (curNote != null && noteDelta != null) {
                return;
            }

            switch (statusMode) {
                case MODE_TIME:
                    if (params[MODE_TIME] < Constants.timeConst - 1) {
                        params[MODE_TIME] += 1;
                    }
                    break;
                case MODE_LEN:
                    if (indCurNoteLen < noteLen.length - 1) {
                        indCurNoteLen++;
                    }
                    calcLen();
                    break;
                case MODE_NOTE:
                    break;
                case MODE_VOL:
                    if (params[MODE_VOL] < 127) {
                        params[MODE_VOL] += 1;
                    }
                    break;
            }
        }

        private void curParamMinus() {
            if (curNote != null && noteDelta != null) {
                return;
            }

            switch (statusMode) {
                case MODE_TIME:
                    if (params[statusMode] > 0) {
                        params[statusMode] -= 1;
                    }
                    break;
                case MODE_LEN:
                    if (indCurNoteLen > 0) {
                        indCurNoteLen--;
                    }
                    calcLen();
                    break;
                case MODE_NOTE:
                    break;
                case MODE_VOL:
                    if (params == absParams) {
                        if (params[statusMode] > 1) {
                            params[statusMode] -= 1;
                        }
                    } else if (params == deltaParams) {
                        if (params[statusMode] > -127) {
                            params[statusMode] -= 1;
                        }
                    }
                    break;
            }
        }

        private void volumePlus() {
            if (curNote != null && noteDelta != null) {
                return;
            }


            if (params[MODE_VOL] < 127) {
                params[MODE_VOL] += 1;
            }

        }

        private void volumeMinus() {
            if (curNote != null && noteDelta != null) {
                return;
            }
            if (params[MODE_VOL] > 0) {
                params[MODE_VOL]--;
            }


        }

        private int getTimeTune() {
            return params[MODE_TIME];
        }

        private byte getVolTune() {
            return (byte) params[MODE_VOL];
        }

        private void calcLen() {
            int len = Constants.timeConst * noteLen[indCurNoteLen];
            switch (extLen) {
                case 0:
                    break;
                case 1:
                    len = len * 3 / 2;
                    break;
                case 2:
                    len = len * 2 / 3;
                    break;
            }
            params[MODE_LEN] = len;
        }

        private int getLenTune() {
            return params[MODE_LEN];
        }

        private void action() {
            if (statusMode == MODE_LEN) {
                extLen = (extLen + 1) % 3;
                calcLen();
            }
        }

        public void paint(Graphics g) {
            g.translate(0 - g.getTranslateX(), statusBegY - g.getTranslateY());
            g.setClip(0, 0, screenWidth, statusHeight);
            boolean isCurNote = false;
            if (isMoveMode == false) {
                noteDelta = null;
                setAbsParams();
                if (!isValidTab()) {
                    nextView();
                }
            } else if (noteDelta == null) {
                noteDelta = new Note(0, (byte) 0, (byte) 0, (byte) 0, 0);
                setDeltaParams();
                resetDelta();
            }
            if (curNote != null || isMoveMode) {
                isCurNote = true;
            }
            int color = (isCurNote) ? 1 : 0;
            g.drawImage(statusImg, 0, statusHeight, Graphics.LEFT | Graphics.BOTTOM);
            if (screenWidth > statusImg.getWidth()) {
                if (isCurNote) {
                    g.setColor(224, 160, 128);
                } else {
                    g.setColor(160, 160, 224);
                }
                g.fillRect(statusImg.getWidth(), 0, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
                g.setColor(255, 255, 255);
                g.fillRect(statusImg.getWidth(), statusHeight - STATUS_BUT_HEIGHT, screenWidth, statusHeight);
                g.setColor(0, 0, 0);
                g.drawLine(statusImg.getWidth(), statusHeight - STATUS_BUT_HEIGHT, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
                g.drawRect(statusImg.getWidth() - 1, 0, screenWidth - statusImg.getWidth(), statusHeight - 1);
            }

            if (statusMode != MODE_NONE) {
                g.setClip(statusMode * STATUS_ONE_BUT, statusHeight - STATUS_BUT_HEIGHT,
                        STATUS_ONE_BUT + 1, STATUS_BUT_HEIGHT);
                if (isCurNote) {
                    g.drawImage(statusImgCur, 0, statusHeight, Graphics.LEFT | Graphics.BOTTOM);
                } else {
                    g.drawImage(statusImgSel, 0, statusHeight, Graphics.LEFT | Graphics.BOTTOM);
                }

                g.setClip(0, 0, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
                if (isCurNote) {
                    g.setColor(224, 160, 128);
                } else {
                    g.setColor(160, 160, 224);
                }
                g.fillRect(0, 0, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
                g.setColor(0);
                g.drawRect(0, 0, screenWidth - 1, statusHeight - STATUS_BUT_HEIGHT);

            } else {
                g.setClip(0, 0, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
                if (isCurNote) {
                    g.drawImage(statusImgDataCur, 0, 0, Graphics.LEFT | Graphics.TOP);
                } else {
                    g.drawImage(statusImgData, 0, 0, Graphics.LEFT | Graphics.TOP);
                }
            }

            g.setClip(0, 0, screenWidth, statusHeight - STATUS_BUT_HEIGHT);
            int yIndent = 10;
            int xIndent = 1;
            int tick;

            if (isMoveMode) {
                note = new Note(getTimeTune(), (byte) channel, (byte) 0,
                        getVolTune(), 0);
            } else if (isCurNote) {
                note = curNote;
            } else {
                note = new Note(getCurTime(), (byte) channel, (byte) getNoteFromLine(0),
                        getVolTune(), getLenTune());
            }
            int m0, m1, m2, m3, m4;
            int meterNom = composition.getNom(), meterDenom = 1 << composition.getDenomE();
            tick = composition.getTicksPer4() * 4 * meterNom / meterDenom;
            int mod = (composition.getTicksPer4() << 2) >> (composition.getDenomE());
            switch (statusMode) {
                case MODE_NONE:
                    m1 = 25;
                    m2 = 50;
                    m3 = 75;
                    m4 = 100;
                    PrintSmallFont.print(g, note.noteTime * 100 / tick, 2, m1 - xIndent, yIndent, color);
                    PrintSmallFont.print(g, note.noteLength * 1000 / tick, 3, m2 - xIndent, yIndent, color);

                    if (MixerCanvas.this instanceof NotesCanvas) {
                        PrintSmallFont.printNote(g, note.noteLine % 12, m3 - xIndent, yIndent, color);
                        PrintSmallFont.print(g, note.noteLine / 12, 0, m3 - xIndent - 12, yIndent, color);
                    } else {
                        PrintSmallFont.print(g, note.noteLine, 0, m3 - xIndent - 12, yIndent, color);
                    }

                    PrintSmallFont.print(g, note.noteVolume, 0, m4 - xIndent, yIndent, color);

                    break;
                case 0:
                    m1 = 25;
                    m2 = 38;
                    m3 = 55;
                    m4 = 70;

                    PrintSmallFont.print(g, note.noteTime / tick, 0, m1, yIndent, color);
                    g.drawLine(m1 + 1, yIndent - 4, m1 + 1, yIndent - 4);
                    g.drawLine(m1 + 1, yIndent - 2, m1 + 1, yIndent - 2);
                    PrintSmallFont.print(g, (note.noteTime * meterNom / tick) % meterNom, 0, m2, yIndent, color);
                    g.drawLine(m2 + 1, yIndent - 4, m2 + 1, yIndent - 4);
                    g.drawLine(m2 + 1, yIndent - 2, m2 + 1, yIndent - 2);
                    PrintSmallFont.print(g, note.noteTime % mod, 0, m3, yIndent, color);
                    PrintSmallFont.print(g, mod, 0, m4, yIndent, color);

                    break;
                case 1:
                    m1 = 41;
                    m2 = 58;
                    m3 = 75;
                    m4 = 90;
                    m0 = 15;

                    if (!isCurNote) {
                        PrintSmallFont.print(g, 1, 0, m0, yIndent, color);
                        g.drawLine(m0 + 1, yIndent, m0 + 3, yIndent - 5);
                        PrintSmallFont.print(g, noteLen[noteLen.length - 1 - indCurNoteLen], 0, m0 + 13, yIndent, color);

                        switch (extLen) {
                            case 0:
                                break;
                            case 1:
                                g.drawImage(imgDot, m1, yIndent + 1,
                                        Graphics.BOTTOM | Graphics.RIGHT);
                                break;
                            case 2:
                                g.drawImage(imgTriplet, m1, yIndent + 1,
                                        Graphics.BOTTOM | Graphics.RIGHT);
                                break;
                        }
                    }

                    PrintSmallFont.print(g, note.noteLength * meterNom / tick, 0, m2, yIndent, color);
                    g.drawLine(m2 + 1, yIndent - 4, m2 + 1, yIndent - 4);
                    g.drawLine(m2 + 1, yIndent - 2, m2 + 1, yIndent - 2);
                    PrintSmallFont.print(g, note.noteLength % mod, 0, m3, yIndent, color);
                    PrintSmallFont.print(g, mod, 0, m4, yIndent, color);

                    break;
                case 2:
                    if (MixerCanvas.this instanceof NotesCanvas) {
                        m3 = 50;
                        m4 = 75;
                        PrintSmallFont.printNote(g, note.noteLine % 12, m3 - xIndent, yIndent, color);
                        PrintSmallFont.print(g, note.noteLine / 12, 0, m3 - xIndent - 12, yIndent, color);

                        PrintSmallFont.print(g, note.noteLine, 0, m4 - xIndent - 12, yIndent, color);
                    } else if (MixerCanvas.this instanceof DrumsCanvas) {
                        m1 = 2;
                        g.setFont(P.smPlain);
                        g.drawString(getNoteFromLine(curY) + " " + Constants.getInstrName(getNoteFromLine(curY) - DrumsCanvas.drumsShift + 145),
                                m1, yIndent + 5, Graphics.BOTTOM | Graphics.LEFT);
                    }

                    break;
                case 3:
                    m4 = 55;

                    if (isMoveMode) {
                        g.drawString(L.str[L.delta], m4 - 18, yIndent + 3, Graphics.BOTTOM | Graphics.RIGHT);
                    }

                    PrintSmallFont.print(g, note.noteVolume, 0, m4 - xIndent, yIndent, color);

                    break;
            }

            needPaintStatus = false;
        }
    }

    private class Options implements CommandListener {

        private Menu menu;
        private Menu mainMenu;
        private int ONE_LINE_HEIGHT = 18;
        private int menuNumLines = 5;
        private int MENU_BEGX = 10;
        private int MENU_BEGY = 10;
        private int MENU_WIDTH = 100;

        public Options() {
            //System.out.println(this.toString() + " created");
            if (screenHeight > 240) {
                ONE_LINE_HEIGHT = 22;
                MENU_WIDTH = 140;
            }

            MENU_BEGX = (screenWidth - MENU_WIDTH) / 2;
            MENU_BEGY = (screenHeight - menuNumLines * ONE_LINE_HEIGHT) / 4;


            mainMenu = new Menu(L.str[L.menu], null, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);

            Menu playMenu = new Menu(L.str[L.playStop], mainMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);
            MenuItem playAllItem = new MenuItem(L.str[L.playAll], 0) {

                public boolean actionPerformed() {
                    try {
                        model.playMix(composition, 0);
                    } catch (Exception e) {
                    }
                    return true;
                }
            };
            playMenu.addItem(playAllItem);
            MenuItem playItem = new MenuItem(L.str[L.playScreen], 0) {

                public boolean actionPerformed() {
                    try {
                        model.playMix(composition, xBase * Constants.timeConst);
                    } catch (Exception e) {
                    }

                    return true;
                }
            };
            playMenu.addItem(playItem);
            MenuItem playTrackAllItem = new MenuItem(L.str[L.trackAll], 0) {

                public boolean actionPerformed() {
                    try {
                        model.playTrack(composition, 0, channel);
                    } catch (Exception e) {
                    }

                    return true;
                }
            };
            playMenu.addItem(playTrackAllItem);
            MenuItem playTrackItem = new MenuItem(L.str[L.trackScreen], 0) {

                public boolean actionPerformed() {
                    try {
                        model.playTrack(composition, xBase * Constants.timeConst, channel);
                    } catch (Exception e) {
                    }

                    return true;
                }
            };
            playMenu.addItem(playTrackItem);
            MenuItem stopItem = new MenuItem(L.str[L.stop], 0) {

                public boolean actionPerformed() {
                    model.stopPlay();
                    return true;
                }
            };
            playMenu.addItem(stopItem);


            mainMenu.addItem(playMenu);

            Menu editMenu = new Menu(L.str[L.edit], mainMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);

            Menu markMenu = new Menu(L.str[L.mark], editMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);
            MenuItem item1 = new MenuItem(L.str[L.markBegin], KEY_NUM1);
            markMenu.addItem(item1);
            MenuItem item2 = new MenuItem(L.str[L.markEnd], KEY_NUM2);
            markMenu.addItem(item2);
            MenuItem item21 = new MenuItem(L.str[L.unmark], 0) {

                public boolean actionPerformed() {
                    setMarkModeOff();
                    return true;
                }
            };
            markMenu.addItem(item21);
            editMenu.addItem(markMenu);

            MenuItem item22 = new MenuItem(L.str[L.modifyBlock], 0) {

                public boolean actionPerformed() {
                    isMoveMode = true;
                    NoteListUtils.deleteSelected(composition, (byte) channel);
                    OrigTime = getCurTime();
                    OrigNote = getCurNote();
                    viewModeDesc(L.str[L.modifyMode]);
                    return true;
                }
            };
            editMenu.addItem(item22);

            MenuItem item3 = new MenuItem(L.str[L.copy], KEY_NUM3);
            editMenu.addItem(item3);

            Menu menuPaste = new Menu(L.str[L.paste], editMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);
            MenuItem item4 = new MenuItem(L.str[L.insert], KEY_NUM4);
            menuPaste.addItem(item4);
            MenuItem item5 = new MenuItem(L.str[L.replace], KEY_NUM5);
            menuPaste.addItem(item5);
            MenuItem item6 = new MenuItem(L.str[L.blend], KEY_NUM6);
            menuPaste.addItem(item6);
            editMenu.addItem(menuPaste);

            MenuItem item8 = new MenuItem(L.str[L.removeSelection], KEY_NUM8);
            editMenu.addItem(item8);
            MenuItem item7 = new MenuItem(L.str[L.delete], KEY_NUM7);
            editMenu.addItem(item7);
            mainMenu.addItem(editMenu);

            Menu undoMenu = new Menu(L.str[L.undo], mainMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);
            MenuItem itemStar = new MenuItem(L.str[L.undo], KEY_STAR);
            undoMenu.addItem(itemStar);
            MenuItem itemPound = new MenuItem(L.str[L.redo], KEY_POUND);
            undoMenu.addItem(itemPound);
            mainMenu.addItem(undoMenu);

            Menu helpMenu = new Menu(L.str[L.help], mainMenu, MENU_BEGX, MENU_BEGY, MENU_WIDTH, menuNumLines, ONE_LINE_HEIGHT);
            
            MenuItem mapItem = new MenuItem(L.str[L.keymap], 0) {

                public boolean actionPerformed() {
                    String[] KEY_MAP = {
                        "<LeftSoftKey>", L.str[L.menu],
                        "<RightSoftKey>", L.str[L.back], 
                        "<Fire>,<5>", L.str[L.insertNoteWithCurrentAttributes] + "\n" + L.str[L.noteAttributeHelp],
                        "<9>,<Clear>", L.str[L.deleteNote],
                        "<Red Phone>", L.str[L.undo],
                        "<Volume +'\'->", L.str[L.noteVolume],
                        "<1>", L.str[L.playFromCurrent],
                        "<3>", L.str[L.playNoteOnCursor],
                        "<7>,<Green Phone>", L.str[L.selectNoteAttribute],
                        "<*>,<#>,<0>", L.str[L.changeNoteAttribute],
                        Rms.numKeysEdit ? "<4>,<6>,<2>,<8>" : "<up>,<down>,<left>,<right>", L.str[L.navigationOnComposition],
                        Rms.numKeysEdit ? "<up>,<down>,<left>,<right>" : "<4>,<6>,<2>,<8>", L.str[L.quicknav]
                    };
                    TextView tv = new TextView(createString(KEY_MAP), L.str[L.keymap], MixerCanvas.this);
                    MixerCanvas.this.display.setCurrent(tv);
                    return true;
                }
            };
            helpMenu.addItem(mapItem);
            MenuItem quickItem = new MenuItem(L.str[L.quickCommands], 0) {

                public boolean actionPerformed() {
                    String[] QUICK_COMMANDS = {
                        "<1> ", L.str[L.markBegin],
                        "<2> ", L.str[L.markEnd],
                        "<3> ", L.str[L.copy],
                        "<4> ", L.str[L.pasteInsert],
                        "<5> ", L.str[L.pasteReplace],
                        "<6> ", L.str[L.pasteOverwrite],
                        "<7> ", L.str[L.shiftDelete],
                        "<8> ", L.str[L.clean],
                        "<9> ", L.str[L.playChannelOnScreen],
                        "<*> ", L.str[L.undo],
                        "<0> ", L.str[L.playChannelAll],
                        "<#> ", L.str[L.redo]
                    };
                    TextView tv = new TextView(createString(QUICK_COMMANDS), L.str[L.quickCommands], MixerCanvas.this);
                    MixerCanvas.this.display.setCurrent(tv);
                    return true;
                }
            };
            helpMenu.addItem(quickItem);
            mainMenu.addItem(helpMenu);
            MenuItem exitItem = new MenuItem(L.str[L.back], 0) {

                public boolean actionPerformed() {
                    Main.midlet.compositionForm.setComposForm();
                    return true;
                }
            };
            mainMenu.addItem(exitItem);

            menu = mainMenu;
        }

        private String createString(String[] strings) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strings.length / 2; i++) {
                sb.append(strings[i * 2]).append(" - ").append(strings[i * 2 + 1]).append('\n');
            }
            return sb.toString();
        }

        public void commandAction(Command c, Displayable displayable) {
            MixerCanvas.this.display.setCurrent(MixerCanvas.this);
        }

        private void resetMenu() {
            menu = mainMenu;
            menu.reset();
        }

        protected void paint(Graphics g) {
            menu.paint(g);
        }
        
        protected void pointerPressed(int x, int y) {
            menu.pointerPressed(x, y);
        }

        private boolean processCommand(int keyCode) {
            switch (keyCode) {
                case KEY_NUM1:
                    timeMarkBeg = getCurTime();
                    setMarkModeOn();
                    break;
                case KEY_NUM2:
                    timeMarkEnd = getCurTime();
                    setMarkModeOn();
                    break;
                case KEY_NUM3:
                    model.getBuffer().copy2Buffer(composition, (byte) channel, timeMarkBeg, timeMarkEnd, false);
                    break;
                case KEY_NUM4:
                    int shiftLen = model.getBuffer().paste2Composition(composition, (byte) channel, getCurTime(), NoteListUtils.INSERT);
                    setMarkModeOff();
                    break;
                case KEY_NUM5:
                    model.getBuffer().paste2Composition(composition, (byte) channel, getCurTime(), NoteListUtils.REPLACE);
                    setMarkModeOff();
                    break;
                case KEY_NUM6:
                    model.getBuffer().paste2Composition(composition, (byte) channel, getCurTime(), NoteListUtils.OVERWRITE);
                    setMarkModeOff();
                    break;
                case KEY_NUM7:
                    if (timeMarkBeg < timeMarkEnd) {
                        model.getBuffer().delete(composition, (byte) channel, timeMarkBeg, timeMarkEnd);
                        setMarkModeOff();
                    }
                    break;
                case KEY_NUM8:

                    NoteListUtils.deleteSelected(composition, (byte) channel);
                    setMarkModeOff();

                    break;
                case KEY_NUM9:
                    try {
                        model.playTrack(composition, xBase * Constants.timeConst, channel);
                    } catch (Exception e) {
                    }
                    break;
                case KEY_NUM0:
                    try {
                        model.playTrack(composition, 0, channel);
                    } catch (Exception e) {
                    }
                    break;
                case KEY_STAR:
                    composition.getUndoableAction().undo();
                    break;
                case KEY_POUND:
                    composition.getUndoableAction().redo();
                    break;
                case -10:
                    
                    break;
                default:
                    if(keyCode == Key.Call) {
                        if (menu.getPrevMenu() != null) {
                            menu = menu.getPrevMenu();
                            return false;
                        }
                    } else {
                        int dir = 0;
                        try {
                            dir = getGameAction(keyCode);
                        } catch (IllegalArgumentException illegArg) {
                        }
                        switch (dir) {
                            case Canvas.UP:
                                menu.up();
                                return false;
                            case Canvas.DOWN:
                                menu.down();
                                return false;
                            case Canvas.RIGHT:
                            case Canvas.FIRE:
                                return menu.actionPerformed();
                            case Canvas.LEFT:
                            case 0:
                                if (menu.getPrevMenu() != null) {
                                    menu = menu.getPrevMenu();
                                    return false;
                                }
                                break;
                            default:
                                return false;
                        }
                    }
            }
            return true;

        }

        protected void keyPressed(int keyCode) {
            if (processCommand(keyCode)) {
                isOptionsView = false;
            }
            runningStatus = true;
            setNeedPaint();
            display.callSerially(MixerCanvas.this);
        }

        /**
         * аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
         */
        private class Menu extends MenuItem {

            private Vector items = null;
            private Menu prevMenu;
            private int x, y, w, h;
            private int hMax;
            private int lineBase, curLine;
            private int numLines;
            private int numLinesMaxVisible;
            private int LINE_HEIGHT;
            private boolean isScroll;

            private Menu(String name, Menu prev,
                    int x1, int y1, int width,
                    int nLines, int onelineHeight) {
                super(name, 0);

                items = new Vector();
                prevMenu = prev;
                x = x1;
                y = y1;
                w = width;
                LINE_HEIGHT = onelineHeight;
                numLinesMaxVisible = nLines;
                numLines = 0;
                h = numLinesMaxVisible * LINE_HEIGHT;
                hMax = h + 1;
                reset();
            }

            private void addItem(MenuItem item) {
                items.addElement(item);
                
                numLines = hMax / LINE_HEIGHT;
                if (numLines >= items.size()) {
                    numLines = items.size();
                    isScroll = false;
                } else {
                    isScroll = true;
                }

                h = numLines * LINE_HEIGHT;
            }

            protected void paint(Graphics g) {
                g.translate(x, y);

                g.setColor(P.colup);
                ui.UI.drawRect(g, P.colup, P.coldn, 0, 1, w, LINE_HEIGHT - 1);
                // g.fillRect(0, 1, w, LINE_HEIGHT - 1);
                g.setColor(0);
                g.drawRect(0, 1, w - 1, LINE_HEIGHT - 1);
                g.setClip(0, 1, w - 1, LINE_HEIGHT - 1);
                g.setColor(255, 255, 255);

                g.setFont(P.medBold);
                g.drawString(this.getName(), (w - 4) / 2, 3, Graphics.TOP | Graphics.HCENTER);
                g.setFont(Font.getDefaultFont());

                g.setClip(0, 0, getWidth(), getHeight());
                g.translate(0, LINE_HEIGHT);

                g.setColor(0);
                g.fillRect(0, 0, w, hMax);
                g.setClip(1, 1, w - 1, hMax - 1);
                MenuItem item;
                for (int i = 0; i < numLinesMaxVisible; i++) {

                    if (i == curLine) {
                        g.setColor(160, 160, 224);
                    } else {
                        g.setGrayScale(255);
                    }

                    g.fillRect(0, i * LINE_HEIGHT + 1, w - 1, LINE_HEIGHT - 1);

                    g.setGrayScale(0);

                    if (i + lineBase < items.size()) {
                        item = (MenuItem) items.elementAt(lineBase + i);
                        g.drawString(item.getName(), 5,
                                i * LINE_HEIGHT + 2, Graphics.TOP | Graphics.LEFT);
                        if (item instanceof Menu) {
                            if (i == curLine) {
                                g.setColor(160, 160, 224);
                            } else {
                                g.setGrayScale(255);
                            }
                            g.fillRect(w - 6, i * LINE_HEIGHT + 1, 3, LINE_HEIGHT - 1);

                            g.setColor(0);
                            int oxt = w - 3;
                            int oyt = i * LINE_HEIGHT + LINE_HEIGHT / 2;
                            for (int odx = 0; odx < 3; ++odx) {
                                g.drawLine(oxt - odx, oyt - odx, oxt - odx, oyt + odx);
                            }

                        }
                    }
                }

            }
            
            private long time = System.currentTimeMillis();
            
            protected void pointerPressed(int x, int y) {
                x -= this.x;
                y -= this.y;
                
                if(x > w) {
                    isOptionsView = false;
                    return;
                }
                curLine = y / LINE_HEIGHT;
                if(curLine >= items.size()) curLine = items.size() - 1;
                else if(curLine < 0) curLine = 0;
                
                if(System.currentTimeMillis() - time > 800) {
                    actionPerformed();
                }
                time = System.currentTimeMillis();
                setNeedPaint();
            }

            public boolean actionPerformed() {
                MenuItem item = (MenuItem) items.elementAt(lineBase + curLine);
                if (item instanceof Menu) {
                    ((Menu) item).reset();
                    MixerCanvas.Options.this.menu = (Menu) item;
                    return false;
                }

                return item.actionPerformed();

            }

            private void up() {
                if (lineBase + curLine > 0) {
                    if (isScroll && curLine <= 1 && lineBase > 0) {
                        lineBase--;
                    } else {
                        curLine--;
                    }
                } else {
                    if (isScroll) {
                        curLine = numLines - 1;
                        lineBase = items.size() - numLines;
                    } else {
                        curLine = items.size() - 1;
                    }
                }
            }

            private void down() {
                if (lineBase + curLine < items.size() - 1) {
                    if (isScroll && curLine >= numLines - 2
                            && lineBase + numLines < items.size()) {
                        lineBase++;
                    } else {
                        curLine++;
                    }
                } else {
                    this.reset();
                }

            }

            private void reset() {
                lineBase = 0;
                curLine = 0;
            }

            private Menu getPrevMenu() {
                return prevMenu;
            }
        }

        /**
         * аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
         */
        class MenuItem {

            /** аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ */
            private String name;
            
            /** аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ */
            private int keyCode;

            /**
             * аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             * @param name аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             * @param key аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             */
            private MenuItem(String name, int key) {
                this.name = name;
                keyCode = key;
            }

            /**
             * аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             * @return аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             */
            protected String getName() {
                return name;
            }

            /**
             * аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             * @return аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
             */
            public boolean actionPerformed() {
                return processCommand(keyCode);
            }
        }
    }
}
