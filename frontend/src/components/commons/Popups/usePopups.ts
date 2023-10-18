import { Slot } from "vue";

interface PopupInfo {
  type: "alert" | "confirm" | "dialog";
  message?: string;
  sidebar?: "left" | "right";
  doneResolve: ((value: unknown) => void) | ((value: boolean) => void);
  slot?: Slot;
}

class Popup {
  static popupDataWrap = {
    popupData: {} as { popupInfo: PopupInfo[] },
  };
}

function usePopups() {
  const push = (info: PopupInfo) => {
    Popup.popupDataWrap.popupData.popupInfo?.push(info);
  };
  return {
    popups: {
      register(data: { popupInfo: PopupInfo[] }) {
        Popup.popupDataWrap.popupData = data;
      },

      alert(msg: string) {
        return new Promise<boolean>((resolve) => {
          push({ type: "alert", message: msg, doneResolve: resolve });
        });
      },

      confirm(msg: string) {
        return new Promise<boolean>((resolve) => {
          push({ type: "confirm", message: msg, doneResolve: resolve });
        });
      },

      dialog(slot?: Slot, sidebar?: "left" | "right") {
        return new Promise((resolve) => {
          push({ type: "dialog", slot, doneResolve: resolve, sidebar });
        });
      },

      done(result: unknown) {
        const popupInfo = Popup.popupDataWrap.popupData.popupInfo?.pop();
        if (!popupInfo) return;
        if (popupInfo.doneResolve) popupInfo.doneResolve(result as boolean);
      },
    },
  };
}

export default usePopups;
export type { PopupInfo };
