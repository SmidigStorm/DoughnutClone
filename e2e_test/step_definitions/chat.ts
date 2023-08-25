/// <reference types="cypress" />
/// <reference types="../support" />
// @ts-check

import { When, Then } from "@badeball/cypress-cucumber-preprocessor"

When("I ask to OpenAI {string}", (askStatement: string) => {
  cy.get("#ask-input").should("be.visible").clear()
  cy.get("#ask-input").type(askStatement)
  cy.get("#ask-button").click()
})

Then("I can confirm the answer {string}", (answer: string) => {
  cy.findByText(answer)
})