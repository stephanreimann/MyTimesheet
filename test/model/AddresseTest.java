/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.*;
import javafx.util.Pair;
import org.junit.*;

/**
 *
 * @author adrest18
 */
public class AddresseTest {

    public AddresseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test()
    public void T00_Parameterized_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String streetname = "Humboldtstrasse";
        Long housenumber = 90L;
        String unitname = "Etage";
        Long unitnumber = 2L;
        String unitlocation = "Rechts";
        String city = "Nürenberg";
        String state = "Bayern"; 
        Long zipcode = 90495L;
        String country = "Deutschland";
        
        //Act
        Address addresse = new Address(id, streetname, housenumber, unitname,
            unitnumber, unitlocation, city, state, zipcode, country);
        
        //Assert
        Assert.assertNotNull(addresse);
        Assert.assertEquals(id, addresse.getId());
        Assert.assertEquals(streetname, addresse.getStreetname());
        Assert.assertEquals(housenumber, addresse.getHousenumber());
        Assert.assertEquals(unitname, addresse.getUnitname());
        Assert.assertEquals(unitnumber, addresse.getUnitnumber());
        Assert.assertEquals(unitlocation, addresse.getUnitlocation());
        Assert.assertEquals(city, addresse.getCity());
        Assert.assertEquals(state, addresse.getState());
        Assert.assertEquals(zipcode, addresse.getZipcode());
        Assert.assertEquals(country, addresse.getCountry());
    }

    @Test()
    public void T01_Copy_Ctor_Called_Returns_WithParameterInitialized_Instance() {
        //Arrange
        Long id = 1L;
        String streetname = "Humboldtstrasse";
        Long housenumber = 90L;
        String unitname = "Etage";
        Long unitnumber = 2L;
        String unitlocation = "Rechts";
        String city = "Nürenberg";
        String state = "Bayern"; 
        Long zipcode = 90495L;
        String country = "Deutschland";
        Address addresse = new Address(id, streetname, housenumber, unitname,
            unitnumber, unitlocation, city, state, zipcode, country);
        
        //Act
        Address copiedAddresse = new Address(addresse);
        
        //Assert
        Assert.assertNotNull(copiedAddresse);
        Assert.assertEquals(addresse.getId(), copiedAddresse.getId());
        Assert.assertEquals(addresse.getStreetname(), copiedAddresse.getStreetname());
        Assert.assertEquals(addresse.getHousenumber(), copiedAddresse.getHousenumber());
        Assert.assertEquals(addresse.getUnitname(), copiedAddresse.getUnitname());
        Assert.assertEquals(addresse.getUnitnumber(), copiedAddresse.getUnitnumber());
        Assert.assertEquals(addresse.getUnitlocation(), copiedAddresse.getUnitlocation());
        Assert.assertEquals(addresse.getCity(), copiedAddresse.getCity());
        Assert.assertEquals(addresse.getState(), copiedAddresse.getState());
        Assert.assertEquals(addresse.getZipcode(), copiedAddresse.getZipcode());
        Assert.assertEquals(addresse.getCountry(), copiedAddresse.getCountry());
    }
    
    @Test()
    public void T10_Requesting_HashCode_Twice_For_Same_Address_Instance_Returns_Identical_HashCode() {
        //Arrange
        Address address = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");

        //Act
        Integer hashCode_FirstCall = address.hashCode();
        Integer hashCode_SecondCall = address.hashCode();
        
        //Assert
        Assert.assertEquals(hashCode_FirstCall, hashCode_SecondCall);
    }
    
    @Test()
    public void T11_Requesting_HashCode_For_Different_Address_Instance_Returns_Different_HashCode() {
        //Arrange
        Address address1 = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        Address address2 = new Address(2L, "Humboldtstrasse", 90L, "Etage",
            2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");

        //Act
        Integer hashCode1 = address1.hashCode();
        Integer hashCode2 = address2.hashCode();

        //Assert
        Assert.assertNotEquals(hashCode2, hashCode1);
    }

    @Test()
    public void T20_Compare_Equal_Addresses_Returns_True() {
        //Arrange
        Address addresse1 = new Address(1L,"Humboldtstrasse", 90L, "Etage",
            2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        
        Address addresse2 = new Address(1L, "Humboldtstrasse", 90L, "Etage",
            2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland");
        
        //Act
        boolean result = addresse1.equals(addresse2);
        
        //Assert
        Assert.assertTrue(result);
    }

    @Test()
    @SuppressWarnings("unchecked")
    public void T21_Compare_Different_Addresses_Returns_False() {
        //Arrange
        List<Pair<Address, Address>> pairList = new ArrayList<>(); 
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "SiemensPlatz", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 91L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Raum", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland")        
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 3L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Links", "Nürenberg", "Bayern", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Fürth", "Bayern", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "NRW", 90495L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 50000L, "Deutschland")
            )
        );
        pairList.add(
            new Pair(
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Deutschland"),
                new Address(1L, "Humboldtstrasse", 90L, "Etage", 2L, "Rechts", "Nürenberg", "Bayern", 90495L, "Niederlande")
            )
        );
        
        for(Pair<Address, Address> pair : pairList) {
            Address addresse1 = pair.getKey();
            Address addresse2 = pair.getValue();
            
            //Act
            boolean result = addresse1.equals(addresse2);

            //Assert
            Assert.assertFalse(result);
        }
    }

    @Test()
    public void T30_Print_Addresse_PrintsResults_In_Expected_PrintFormat() {
        //Arrange
        String expectedPrintResult = "Humboldtstrasse, 90, Etage, 2, Rechts, Nürenberg, Bayern, 90495, Deutschland";
        
        Long id = 1L;
        String streetname = "Humboldtstrasse";
        Long housenumber = 90L;
        String unitname = "Etage";
        Long unitnumber = 2L;
        String unitlocation = "Rechts";
        String city = "Nürenberg";
        String state = "Bayern"; 
        Long zipcode = 90495L;
        String country = "Deutschland";

        Address addresse = new Address(id, streetname, housenumber, unitname,
            unitnumber, unitlocation, city, state, zipcode, country);
        
        //Act
        String printResult = addresse.toString();
        
        //Assert
        Assert.assertEquals(expectedPrintResult, printResult);       
    }
    
}
