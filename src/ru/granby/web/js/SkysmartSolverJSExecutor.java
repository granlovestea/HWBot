package ru.granby.web.js;

import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.granby.model.entity.skysmart.SkysmartStep;
import ru.granby.utils.FileUtils;

import java.io.IOException;

public class SkysmartSolverJSExecutor {
    private static final Logger log = LoggerFactory.getLogger(SkysmartSolverJSExecutor.class);
    private static String katexScript;
    private static String answersMarkerScript;
    private JavascriptExecutor javascriptExecutor;

    static {
        try {
            katexScript = FileUtils.getFileContent(System.getenv("SOLVER_EXTENSION_SRC_PATH") + "\\skysmart\\katex.min.js");
            answersMarkerScript = FileUtils.getFileContent(System.getenv("SOLVER_EXTENSION_SRC_PATH") + "\\skysmart\\answers-marker.js");
        } catch (IOException e) {
            log.error("Can't get scripts content");
        }
    }

    public SkysmartSolverJSExecutor(JavascriptExecutor javascriptExecutor) {
        this.javascriptExecutor = javascriptExecutor;
    }

    public void executeStepSolving(SkysmartStep step) {
        String script = katexScript + answersMarkerScript +
                String.format(
                        "var solvedStepContent = AnswersMarker.mark(`%s`);\n" +
                        "document.head.innerHTML = solvedStepContent.head.innerHTML;\n" +
                        "document.body.innerHTML = solvedStepContent.body.innerHTML;\n",
                        step.getContent()
                );
        javascriptExecutor.executeScript(script);
    }
}
