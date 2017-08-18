package edu.umich.its.spe;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	SPEEsbGETGradesIntegrationTest.class,
	SPEEsbPUTGradeIntegrationTest.class,
	//SPEEsbTest.class,
	SPEEsbTokenTest.class })
public class ASuiteOfESBTests {

}
