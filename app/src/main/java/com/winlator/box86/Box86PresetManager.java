package com.winlator.box86;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.preference.PreferenceManager;

import com.winlator.R;
import com.winlator.core.EnvVars;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Box86PresetManager {
    public static EnvVars getEnvVars(Context context, String id) {
        EnvVars envVars = new EnvVars();

        if (id.equals(Box86Preset.STABILITY)) {
            envVars.put("BOX86_DYNAREC_SAFEFLAGS", "2");
            envVars.put("BOX86_DYNAREC_FASTNAN", "0");
            envVars.put("BOX86_DYNAREC_FASTROUND", "0");
            envVars.put("BOX86_DYNAREC_X87DOUBLE", "1");
            envVars.put("BOX86_DYNAREC_BIGBLOCK", "0");
            envVars.put("BOX86_DYNAREC_STRONGMEM", "2");
            envVars.put("BOX86_DYNAREC_FORWARD", "128");
            envVars.put("BOX86_DYNAREC_CALLRET", "0");
            envVars.put("BOX86_DYNAREC_WAIT", "0");
            envVars.put("BOX86_DYNAREC_NATIVEFLAGS", "0");
            envVars.put("BOX86_DYNAREC_WEAKBARRIER", "0");
        }
        else if (id.equals(Box86Preset.CONSERVATIVE)) {
            envVars.put("BOX86_DYNAREC_SAFEFLAGS", "2");
            envVars.put("BOX86_DYNAREC_FASTNAN", "0");
            envVars.put("BOX86_DYNAREC_FASTROUND", "0");
            envVars.put("BOX86_DYNAREC_X87DOUBLE", "1");
            envVars.put("BOX86_DYNAREC_BIGBLOCK", "1");
            envVars.put("BOX86_DYNAREC_STRONGMEM", "1");
            envVars.put("BOX86_DYNAREC_FORWARD", "128");
            envVars.put("BOX86_DYNAREC_CALLRET", "0");
            envVars.put("BOX86_DYNAREC_WAIT", "1");
            envVars.put("BOX86_DYNAREC_NATIVEFLAGS", "0");
            envVars.put("BOX86_DYNAREC_WEAKBARRIER", "1");
        }
        else if (id.equals(Box86Preset.INTERMEDIATE)) {
            envVars.put("BOX86_DYNAREC_SAFEFLAGS", "2");
            envVars.put("BOX86_DYNAREC_FASTNAN", "1");
            envVars.put("BOX86_DYNAREC_FASTROUND", "0");
            envVars.put("BOX86_DYNAREC_X87DOUBLE", "1");
            envVars.put("BOX86_DYNAREC_BIGBLOCK", "2");
            envVars.put("BOX86_DYNAREC_STRONGMEM", "0");
            envVars.put("BOX86_DYNAREC_FORWARD", "128");
            envVars.put("BOX86_DYNAREC_CALLRET", "0");
            envVars.put("BOX86_DYNAREC_WAIT", "1");
            envVars.put("BOX86_DYNAREC_NATIVEFLAGS", "0");
            envVars.put("BOX86_DYNAREC_WEAKBARRIER", "2");
        }
        else if (id.equals(Box86Preset.PERFORMANCE)) {
            envVars.put("BOX86_DYNAREC_SAFEFLAGS", "1");
            envVars.put("BOX86_DYNAREC_FASTNAN", "1");
            envVars.put("BOX86_DYNAREC_FASTROUND", "1");
            envVars.put("BOX86_DYNAREC_X87DOUBLE", "0");
            envVars.put("BOX86_DYNAREC_BIGBLOCK", "3");
            envVars.put("BOX86_DYNAREC_STRONGMEM", "0");
            envVars.put("BOX86_DYNAREC_FORWARD", "512");
            envVars.put("BOX86_DYNAREC_CALLRET", "1");
            envVars.put("BOX86_DYNAREC_WAIT", "1");
            envVars.put("BOX86_DYNAREC_NATIVEFLAGS", "1");
            envVars.put("BOX86_DYNAREC_WEAKBARRIER", "2");
        }
        else if (id.startsWith(Box86Preset.CUSTOM)) {
            for (String[] preset : customPresetsIterator(context)) {
                if (preset[0].equals(id)) {
                    envVars.putAll(preset[2]);
                    break;
                }
            }
        }

        return envVars;
    }

    public static ArrayList<Box86Preset> getPresets(Context context) {
        ArrayList<Box86Preset> presets = new ArrayList<>();
        presets.add(new Box86Preset(Box86Preset.STABILITY, context.getString(R.string.stability)));
        presets.add(new Box86Preset(Box86Preset.CONSERVATIVE, context.getString(R.string.conservative)));
        presets.add(new Box86Preset(Box86Preset.INTERMEDIATE, context.getString(R.string.intermediate)));
        presets.add(new Box86Preset(Box86Preset.PERFORMANCE, context.getString(R.string.performance)));
        for (String[] preset : customPresetsIterator(context)) presets.add(new Box86Preset(preset[0], preset[1]));
        return presets;
    }

    public static Box86Preset getPreset(Context context, String id) {
        for (Box86Preset preset : getPresets(context)) if (preset.id.equals(id)) return preset;
        return null;
    }

    private static Iterable<String[]> customPresetsIterator(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String customPresetsStr = preferences.getString("box86_custom_presets", "");
        final String[] customPresets = customPresetsStr.split(",");
        final int[] index = {0};
        return () -> new Iterator<String[]>() {
            @Override
            public boolean hasNext() {
                return index[0] < customPresets.length && !customPresetsStr.isEmpty();
            }

            @Override
            public String[] next() {
                return customPresets[index[0]++].split("\\|");
            }
        };
    }

    public static int getNextPresetId(Context context) {
        int maxId = 0;
        for (String[] preset : customPresetsIterator(context)) {
            maxId = Math.max(maxId, Integer.parseInt(preset[0].replace(Box86Preset.CUSTOM+"-", "")));
        }
        return maxId+1;
    }

    public static void editPreset(Context context, String id, String name, EnvVars envVars) {
        final String key = "box86_custom_presets";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String customPresetsStr = preferences.getString(key, "");

        if (id != null) {
            String[] customPresets = customPresetsStr.split(",");
            for (int i = 0; i < customPresets.length; i++) {
                String[] preset = customPresets[i].split("\\|");
                if (preset[0].equals(id)) {
                    customPresets[i] = id+"|"+name+"|"+envVars.toString();
                    break;
                }
            }
            customPresetsStr = String.join(",", customPresets);
        }
        else {
            String preset = Box86Preset.CUSTOM+"-"+getNextPresetId(context)+"|"+name+"|"+envVars.toString();
            customPresetsStr += (!customPresetsStr.isEmpty() ? "," : "")+preset;
        }
        preferences.edit().putString(key, customPresetsStr).apply();
    }

    public static void duplicatePreset(Context context, String id) {
        ArrayList<Box86Preset> presets = getPresets(context);
        Box86Preset originPreset = null;
        for (Box86Preset preset : presets) {
            if (preset.id.equals(id)) {
                originPreset = preset;
                break;
            }
        }
        if (originPreset == null) return;

        String newName;
        for (int i = 1;;i++) {
            newName = originPreset.name+" ("+i+")";
            boolean found = false;
            for (Box86Preset preset : presets) {
                if (preset.name.equals(newName)) {
                    found = true;
                    break;
                }
            }
            if (!found) break;
        }

        editPreset(context, null, newName, getEnvVars(context, originPreset.id));
    }

    public static void removePreset(Context context, String id) {
        final String key = "box86_custom_presets";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String oldCustomPresetsStr = preferences.getString(key, "");
        String newCustomPresetsStr = "";

        String[] customPresets = oldCustomPresetsStr.split(",");
        for (int i = 0; i < customPresets.length; i++) {
            String[] preset = customPresets[i].split("\\|");
            if (!preset[0].equals(id)) newCustomPresetsStr += (!newCustomPresetsStr.isEmpty() ? "," : "")+customPresets[i];
        }

        preferences.edit().putString(key, newCustomPresetsStr).apply();
    }

    public static void loadSpinner(Spinner spinner, String selectedId) {
        Context context = spinner.getContext();
        ArrayList<Box86Preset> presets = getPresets(context);

        int selectedPosition = 0;
        for (int i = 0; i < presets.size(); i++) {
            if (presets.get(i).id.equals(selectedId)) {
                selectedPosition = i;
                break;
            }
        }

        spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, presets));
        spinner.setSelection(selectedPosition);
    }

    public static String getSpinnerSelectedId(Spinner spinner) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int selectedPosition = spinner.getSelectedItemPosition();
        if (adapter != null && adapter.getCount() > 0 && selectedPosition >= 0) {
            return ((Box86Preset)adapter.getItem(selectedPosition)).id;
        }
        else return Box86Preset.DEFAULT;
    }
}
