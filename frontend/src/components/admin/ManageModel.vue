<template>
  <ManageModelInner
    v-bind="{ modelList, selectedModels }"
    v-if="modelList && selectedModels"
    @save="save"
  />
  <LoadingPage v-else />
</template>
<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { GlobalAiModelSettings } from "@/generated/backend";
import useLoadingApi from "@/managedApi/useLoadingApi";
import LoadingPage from "@/pages/commons/LoadingPage.vue";
import ManageModelInner from "./ManageModelInner.vue";

const { managedApi } = useLoadingApi();
const modelList = ref<string[] | undefined>(undefined);
const selectedModels = ref<GlobalAiModelSettings | undefined>(undefined);

onMounted(() => {
  Promise.all([
    managedApi.restAiController.getAvailableGptModels(),
    managedApi.restGlobalSettingsController.getCurrentModelVersions(),
  ]).then((results) => {
    const [modelListRes, selectedModelRes] = results;
    modelList.value = modelListRes;
    selectedModels.value = selectedModelRes;
  });
});

const save = async (settings: GlobalAiModelSettings) => {
  selectedModels.value =
    await managedApi.restGlobalSettingsController.setCurrentModelVersions(
      settings,
    );
};
</script>
