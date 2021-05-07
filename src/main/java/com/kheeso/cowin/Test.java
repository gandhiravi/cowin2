package com.kheeso.cowin;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Test {

    public static void main(String args[]){
        final Runnable runnable =
                (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if (runnable != null) runnable.run();
    }
}
