import { assumeNotePage } from "./notePage"
import NotePath from "../../support/NotePath"

export const routerToNotebooksPage = () => {
  cy.routerPush("/notebooks", "notebooks", {})
  return {
    navigateToPath(notePath: NotePath) {
      return notePath.path.reduce(
        (page, noteTopic) => page.navigateToChild(noteTopic),
        assumeNotePage(),
      )
    },
  }
}