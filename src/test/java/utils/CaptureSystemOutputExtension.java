package utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit JUpiter extension for capturing output to {@code System.out} and
 * {@code System.err} with expectations supported via Hamcrest matchers.
 *
 * <p>Based on code from Spring Boot's
 * <a href="https://github.com/spring-projects/spring-boot/blob/d3c34ee3d1bfd3db4a98678c524e145ef9bca51c/spring-boot-project/spring-boot-tools/spring-boot-test-support/src/main/java/org/springframework/boot/testsupport/rule/OutputCapture.java">OutputCapture</a>
 * rule for JUnit 4 by Phillip Webb and Andy Wilkinson.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
class CaptureSystemOutputExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getOutputCapture(context).captureOutput();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CaptureSystemOutput.OutputCapture outputCapture = getOutputCapture(context);
        try {
            if (!outputCapture.matchers.isEmpty()) {
                String output = outputCapture.toString();
                assertThat(output, allOf(outputCapture.matchers));
            }
        }
        finally {
            outputCapture.releaseOutput();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        boolean isTestMethodLevel = extensionContext.getTestMethod().isPresent();
        boolean isOutputCapture = parameterContext.getParameter().getType() == CaptureSystemOutput.OutputCapture.class;
        return isTestMethodLevel && isOutputCapture;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getOutputCapture(extensionContext);
    }

    private CaptureSystemOutput.OutputCapture getOutputCapture(ExtensionContext context) {
        return getStore(context).getOrComputeIfAbsent(CaptureSystemOutput.OutputCapture.class);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

}