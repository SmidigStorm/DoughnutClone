<template>
  <div style="display: flex">
    <NoteEditableTopic
      :note-id="note.id"
      :note-topic-constructor="note.topicConstructor"
      :note-topic="note.topic"
      :storage-accessor="storageAccessor"
    />
    <slot name="topic-additional" />
    <button
      class="btn btn-sm download-btn"
      @click="downloadAudioFile(note.noteAccessories.audioId!)"
      v-if="note.noteAccessories.audioName"
    >
      Download {{ note.noteAccessories.audioName }}
    </button>
  </div>
  <div role="details" class="note-content">
    <NoteEditableDetails
      :note-id="note.id"
      :note-details="note.details"
      :storage-accessor="storageAccessor"
    />
    <slot name="note-content-other" />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Note } from "@/generated/backend";
import { type StorageAccessor } from "../../store/createNoteStorage";
import NoteEditableTopic from "./NoteEditableTopic.vue";
import NoteEditableDetails from "./NoteEditableDetails.vue";

export default defineComponent({
  props: {
    note: { type: Object as PropType<Note>, required: true },
    storageAccessor: {
      type: Object as PropType<StorageAccessor>,
      required: true,
    },
  },
  components: {
    NoteEditableTopic,
    NoteEditableDetails,
  },
  methods: {
    async downloadAudioFile(audioId: number) {
      const audioUrl = `/attachments/audio/${audioId}`;

      const link = document.createElement("a");
      link.href = audioUrl;

      link.download = this.note.noteAccessories.audioName!;

      link.click();
    },
  },
});
</script>
<style scoped>
.download-btn {
  text-decoration: underline;
}
</style>
