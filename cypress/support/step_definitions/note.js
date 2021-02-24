import {
  Given,
  And,
  Then,
  When,
  Before,
} from "cypress-cucumber-preprocessor/steps";

Given("there are some notes for the current user", (data) => {
  cy.seedNotes(data.hashes());
})

When("I create note with:", (data) => {
  cy.createNotes(data.hashes());
});

Then("I should see these notes belonging to the user at the top level of all my notes", (data) => {
    cy.visit("/notes");
    cy.expectNotes(data);
});

Then("I should see these notes belonging to the user", (data) => {
    cy.expectNotes(data);
});

When("I create note belonging to {string}:", (noteTitle, data) => {
  cy.createNotes(data.hashes(), noteTitle);
});

Then("I should see {string} in note title", (noteTitle) => {
    cy.findByText(noteTitle);
});

Then("I should not see note {string} at the top level of all my notes", (noteTitle) => {
    cy.visit("/notes");
    cy.findByText("Top Level Notes");
    cy.findByText(noteTitle).should('not.exist');
});

When("I open {string} note", (noteTitle) => {
    cy.findByText(noteTitle).click();
});
