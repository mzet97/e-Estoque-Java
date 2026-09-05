package io.github.mzet97.eestoque;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import org.springframework.modulith.events.Externalized;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

/**
 * Regras arquiteturais globais (§96), complementares ao
 * ModulithVerificationTest (fronteiras por módulo). Com injeção direta de
 * handlers, o web conhece application; nada do web bypassa a application
 * para falar com persistência ou mensageria.
 */
@AnalyzeClasses(packages = "io.github.mzet97.eestoque", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainDoesNotDependOnDeliveryMechanisms = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..web..", "..persistence..", "..messaging..",
                    "org.springframework.web..", "org.springframework.data..",
                    "jakarta.persistence..", "jakarta.servlet..");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnWebOrPersistence = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..web..", "..persistence..", "org.springframework.web..",
                    "jakarta.servlet..", "jakarta.persistence..", "org.springframework.data..");

    @ArchTest
    static final ArchRule webDoesNotBypassApplication = noClasses()
            .that().resideInAPackage("..web..")
            .should().dependOnClassesThat().resideInAnyPackage("..persistence..", "..messaging..");

    @ArchTest
    static final ArchRule applicationComponentsAreHandlers = classes()
            .that().resideInAPackage("..application..").and().areAnnotatedWith(Component.class)
            .should().haveSimpleNameEndingWith("Handler");

    @ArchTest
    static final ArchRule externalizedEventsLiveInDomain = classes()
            .that().areAnnotatedWith(Externalized.class)
            .should().resideInAPackage("..domain..");

    @ArchTest
    static final ArchRule injectionIsConstructorBased = noFields()
            .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired");
}
