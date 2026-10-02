package com.pharmacy.api;

import jakarta.ws.rs.core.Application;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PharmacyApplicationTest {

    // -----------------------------------------------------------------------
    // getClasses
    // -----------------------------------------------------------------------

    @Test
    void getClasses_calledOnNewInstance_returnsAllFiveRegisteredClasses() {
        // Arrange
        PharmacyApplication application = new PharmacyApplication();

        // Act
        Set<Class<?>> classes = application.getClasses();

        // Assert
        assertThat(classes).containsExactlyInAnyOrder(
                DashboardResource.class,
                MedicineResource.class,
                PrescriptionResource.class,
                OrderResource.class,
                CorsFilter.class);
    }

    @Test
    void getClasses_calledTwice_returnsFreshSetEachTime() {
        // Arrange
        PharmacyApplication application = new PharmacyApplication();

        // Act
        Set<Class<?>> first  = application.getClasses();
        Set<Class<?>> second = application.getClasses();

        // Assert – sets are equal in content but are independent instances
        assertThat(first).isEqualTo(second);
        assertThat(first).isNotSameAs(second);
    }

    @Test
    void pharmacyApplication_isInstanceOfJaxRsApplication() {
        // Arrange / Act
        PharmacyApplication application = new PharmacyApplication();

        // Assert
        assertThat(application).isInstanceOf(Application.class);
    }
}
