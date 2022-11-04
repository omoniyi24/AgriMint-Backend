package com.github.agrimint;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.github.agrimint");

        noClasses()
            .that()
            .resideInAnyPackage("com.github.agrimint.service..")
            .or()
            .resideInAnyPackage("com.github.agrimint.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.github.agrimint.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
