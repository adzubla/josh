package josh.shell;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ShellTest {

    private Shell shell;

    @Before
    public void init() {
        shell = spy(new Shell());
    }

    @Test
    public void testExpandVariables() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "Test", "--company", "${COMP_NAME}"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("Test", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("COMPANY_01", expandedArgs.get(3));
    }

    @Test
    public void testExpandVariables_UsingTwoDifferentProperties() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");
        when(shell.getPropertyValue("NAME")).thenReturn("NAME_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "${NAME}", "--company", "${COMP_NAME}"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("NAME_01", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("COMPANY_01", expandedArgs.get(3));
    }

    @Test
    public void testExpandVariables_UsingTheSamePropertyOnManyVariables() throws Exception {
        when(shell.getPropertyValue("NAME")).thenReturn("NAME_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "${NAME}", "--company", "${NAME}"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("NAME_01", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("NAME_01", expandedArgs.get(3));
    }

    @Test
    public void testExpandVariables_WithNoPropertyRegistered() throws Exception {
        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "Test", "--company", "${NAME}"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("Test", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("", expandedArgs.get(3));
    }

    @Test
    public void testExpandVariables_UsingManyPropertiesOnTheSameVariable() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");
        when(shell.getPropertyValue("NAME")).thenReturn("NAME_01");
        when(shell.getPropertyValue("USER")).thenReturn("TEST_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--company", "${NAME} ${USER} ${COMP_NAME}"));

        assertEquals("Wrong number of variables", 2, expandedArgs.size());
        assertEquals("--company", expandedArgs.get(0));
        assertEquals("NAME_01 TEST_01 COMPANY_01", expandedArgs.get(1));
    }

    @Test
    public void testExpandVariables_UsingManyPropertiesMixedOnTheSameVariable() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");
        when(shell.getPropertyValue("NAME")).thenReturn("NAME_01");
        when(shell.getPropertyValue("USER")).thenReturn("TEST_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--company", "${NAME} ${USER} ${COMP_NAME} ${NAME}"));

        assertEquals("Wrong number of variables", 2, expandedArgs.size());
        assertEquals("--company", expandedArgs.get(0));
        assertEquals("NAME_01 TEST_01 COMPANY_01 NAME_01", expandedArgs.get(1));
    }

    @Test
    public void testExpandVariables_UsingManyPropertiesOnTheSameVariable_OnePropertyIsNotRegistered() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");
        when(shell.getPropertyValue("NAME")).thenReturn("NAME_01");
        when(shell.getPropertyValue("USER")).thenReturn(null);

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--company", "${NAME} ${USER} ${COMP_NAME}"));

        assertEquals("Wrong number of variables", 2, expandedArgs.size());
        assertEquals("--company", expandedArgs.get(0));
        assertEquals("NAME_01  COMPANY_01", expandedArgs.get(1));
    }

    @Test
    public void testExpandVariables_WithoutVariables() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "Test", "--company", "Company_01"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("Test", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("Company_01", expandedArgs.get(3));
    }

    @Test
    public void testExpandVariables_WithoutClosing() throws Exception {
        when(shell.getPropertyValue("COMP_NAME")).thenReturn("COMPANY_01");

        List<String> expandedArgs = shell.expandVariables(Arrays.asList("--name", "Test", "--company", "${COMP_NAME TESTE"));

        assertEquals("Wrong number of variables", 4, expandedArgs.size());
        assertEquals("--name", expandedArgs.get(0));
        assertEquals("Test", expandedArgs.get(1));
        assertEquals("--company", expandedArgs.get(2));
        assertEquals("${COMP_NAME TESTE", expandedArgs.get(3));
    }
}