package edu.umich.its.spe;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FileIOTest.class, PersistBlobImplTest.class, PersistTimestampTest.class,
	SPEEsbPropertiesTest.class,
	SPEMasterProcessGradeTest.class, SPESummaryTest.class })
public class ASuiteOfMethodTests {

}
