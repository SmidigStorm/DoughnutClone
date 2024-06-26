<template>
  <ToolbarFrame>
    <div class="btn-group btn-group-sm">
      <NoteNewButton
        button-title="Add Child Note"
        v-bind="{ parentId: note.id, storageAccessor }"
      >
        <SvgAddChild />
      </NoteNewButton>

      <PopButton title="edit note">
        <template #button_face>
          <SvgEdit />
        </template>
        <template #default="{ closer }">
          <NoteEditAccessoriesDialog
            v-bind="{ note, storageAccessor }"
            @close-dialog="closer"
          />
        </template>
      </PopButton>

      <PopButton title="Upload audio">
        <template #button_face>
          <SvgResume />
        </template>
        <template #default="{ closer }">
          <NoteEditUploadAudioDialog
            v-bind="{ note, storageAccessor }"
            @close-dialog="closer"
          />
        </template>
      </PopButton>

      <PopButton title="associate wikidata">
        <template #button_face>
          <SvgWikidata />
        </template>
        <template #default="{ closer }">
          <WikidataAssociationDialog
            v-bind="{ note, storageAccessor }"
            @close-dialog="closer"
          />
        </template>
      </PopButton>
      <NoteDetailsAutoCompletionButton v-bind="{ note, storageAccessor }" />
      <PopButton title="search and link note">
        <template #button_face>
          <SvgSearchForLink />
        </template>
        <template #default="{ closer }">
          <LinkNoteDialog
            v-bind="{ note, storageAccessor }"
            @close-dialog="closer"
          />
        </template>
      </PopButton>
      <div class="dropdown">
        <button
          id="dropdownMenuButton"
          aria-expanded="false"
          aria-haspopup="true"
          class="btn dropdown-toggle"
          data-bs-toggle="dropdown"
          role="button"
          title="more options"
        >
          <SvgCog />
        </button>
        <div class="dropdown-menu dropdown-menu-end">
          <PopButton
            btn-class="dropdown-item btn-primary"
            title="Generate Image with DALL-E"
          >
            <AIGenerateImageDialog v-bind="{ note, storageAccessor }" />
          </PopButton>
          <NoteDeleteButton
            class="dropdown-item"
            v-bind="{ noteId: note.id, storageAccessor }"
          />
        </div>
      </div>
    </div>
  </ToolbarFrame>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { StorageAccessor } from "@/store/createNoteStorage";
import { Note } from "@/generated/backend";
import NoteNewButton from "./NoteNewButton.vue";
import SvgAddChild from "../svgs/SvgAddChild.vue";
import SvgEdit from "../svgs/SvgEdit.vue";
import NoteEditAccessoriesDialog from "../notes/NoteEditAccessoriesDialog.vue";
import NoteEditUploadAudioDialog from "../notes/NoteEditUploadAudioDialog.vue";
import SvgWikidata from "../svgs/SvgWikidata.vue";
import WikidataAssociationDialog from "../notes/WikidataAssociationDialog.vue";
import SvgSearchForLink from "../svgs/SvgSearchForLink.vue";
import LinkNoteDialog from "../links/LinkNoteDialog.vue";
import SvgCog from "../svgs/SvgCog.vue";
import NoteDeleteButton from "./NoteDeleteButton.vue";
import PopButton from "../commons/Popups/PopButton.vue";
import AIGenerateImageDialog from "../notes/AIGenerateImageDialog.vue";
import NoteDetailsAutoCompletionButton from "./NoteDetailsAutoCompletionButton.vue";
import SvgResume from "../svgs/SvgResume.vue";

export default defineComponent({
  props: {
    storageAccessor: {
      type: Object as PropType<StorageAccessor>,
      required: true,
    },
    note: {
      type: Object as PropType<Note>,
      required: true,
    },
  },
  components: {
    NoteNewButton,
    SvgAddChild,
    SvgEdit,
    NoteEditAccessoriesDialog,
    NoteEditUploadAudioDialog,
    SvgWikidata,
    WikidataAssociationDialog,
    SvgSearchForLink,
    LinkNoteDialog,
    SvgCog,
    SvgResume,
    NoteDeleteButton,
    PopButton,
    AIGenerateImageDialog,
    NoteDetailsAutoCompletionButton,
  },
});
</script>
