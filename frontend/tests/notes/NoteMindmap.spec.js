/**
 * @jest-environment jsdom
 */
import { screen } from "@testing-library/vue";
import NoteMinmap from "@/components/notes/mindmap/NoteMindmap.vue";
import store from "../../src/store/index.js";
import { renderWithStoreAndMockRoute } from "../helpers";
import makeMe from "../fixtures/makeMe";

describe("note mindmap", () => {

  it("should render one note", async () => {
    const note = makeMe.aNote.title("single note").shortDescription('not long').please();
    store.commit("loadNotes", [note]);
    renderWithStoreAndMockRoute(
      store,
      NoteMinmap,
      { props: { noteId: note.id } },
    );
    expect(screen.getByRole("card")).toHaveTextContent("single note");
    expect(screen.getByRole("card")).toHaveTextContent("not long");
  });

  it("should render two notes", async () => {
    const note = makeMe.aNote.title("note1").please();
    const childNote = makeMe.aNote.title("note2").under(note).please();
    store.commit("loadNotes", [note, childNote]);
    const { wrapper } = renderWithStoreAndMockRoute(
      store,
      NoteMinmap,
      { props: { noteId: note.id } },
    );
    expect(screen.getAllByRole("card")).toHaveLength(2)

    const connection = await wrapper.container.querySelector("svg.mindmap-canvas")
    const line = connection.querySelector("line")
    expect(line.getAttribute("x1")).toEqual("0")
    expect(parseFloat(line.getAttribute("x2"))).toBeCloseTo(0)
    expect(parseFloat(line.getAttribute("y2"))).toBeCloseTo(-210)
  });

})
