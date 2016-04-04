/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.

 */
package html5;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.sql.DataSource;

/**
 * Simple bean for storing form field values.
 */
@ManagedBean
@SessionScoped
public class Bean implements Serializable {

    @Resource(lookup = "java:/PawDS")
    private DataSource dataSource;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalNr() {
        return personalNr;
    }

    public void setPersonalNr(String personalNr) {
        this.personalNr = personalNr;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(String houseNr) {
        this.houseNr = houseNr;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    private String firstName = "";

    private String lastName = "";

    private String email = "";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {return birthday; }

    public void setBirthday(String birthday) { this.birthday = birthday; }


    private String personalNr = "";
    private String street = "";
    private String houseNr = "";
    private String town = "";
    private String postalCode = "";
    private String birthday = "";


    @Override
    public String toString() {
        return "Bean{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", personalNr='" + personalNr + '\'' +
                ", street='" + street + '\'' +
                ", houseNr='" + houseNr + '\'' +
                ", town='" + town + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

    public String submit() {

        System.out.println(this);

        try {
            Connection conn = dataSource.getConnection();
            Statement sta = conn.createStatement();
            sta.executeUpdate("INSERT INTO students (firstName, lastName) VALUES ('" + firstName + "','" + lastName + "')");
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "confirmation";
    }


    public void validatePersonalNr(FacesContext facesContext,
                                   UIComponent uiComponent, Object value) {
        String number = (String) value;
        PeselValidator validator = new PeselValidator(number);
        if (!validator.isValid()){
            ((UIInput) uiComponent).setValid(false);

            FacesMessage message = new FacesMessage("Nieprawidłowy pesel");
            facesContext.addMessage(uiComponent.getClientId(facesContext), message);
        }
    }

    public void personalNrAgainstBirthdayValidation (ComponentSystemEvent event) {

        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();

        //pobieranie peselu i daty urodzenia
        UIInput uiInputPersonalNr = (UIInput) components.findComponent("personal_nr");
        UIInput uiInputBirthday = (UIInput) components.findComponent("birthday");

        String pesel = (String) uiInputPersonalNr.getValue();
        PeselValidator validator = new PeselValidator(pesel);
        int pBirthY = validator.getBirthYear();
        int pBirthM = validator.getBirthMonth();
        int pBirthD = validator.getBirthDay();

        Date date = (Date) uiInputBirthday.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int bBirthY = calendar.get(Calendar.YEAR);
        int bBirthM = calendar.get(Calendar.MONTH) + 1;
        int bBirthD = calendar.get(Calendar.DAY_OF_MONTH);

        if (pBirthY != bBirthY || pBirthM != bBirthM || pBirthD != bBirthD) {
            FacesMessage message = new FacesMessage("Pesel niezgodny z datą urodzenia");
            fc.addMessage(uiInputPersonalNr.getClientId(fc), message);
        }

    }
}

