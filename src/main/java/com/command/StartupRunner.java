package com.command;

import com.job.QuatzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Dell on 2019/5/15.
 */
@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    QuatzManager quatzManager;
    @Override
    public void run(String... strings) throws Exception {
        quatzManager.loadJOB();
    }
}
