package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.PrintStream;

public class TestBase {
    protected Logger logger;

    protected RequestSpecification requestSpecification;
    protected String baseUrl;
    protected String resourceUsers;

    @BeforeMethod
    public void beforeMethod(){
        initLogger();
        initVariables();
        initSpecConsoleOutput();
    }

    @AfterMethod(onlyForGroups = {"DeleteUserNeeded"})
    public void afterMethodSpecialGroup(ITestContext iTestContext){
        logger.info("Deleting user id: " + iTestContext.getAttribute("userToDelete"));
    }

    private void initLogger() {
        logger = LogManager.getLogger();
    }

    private void initSpecConsoleOutput(){
        this.requestSpecification = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    private void initSpecLoggerOutput(){
        PrintStream logStream = IoBuilder.forLogger(logger).buildPrintStream();
        this.requestSpecification = new RequestSpecBuilder()
                .addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream))
                .build();
    }

    private void initVariables(){
        this.baseUrl = "https://reqres.in";
        this.resourceUsers = "/api/users";
    }
}
