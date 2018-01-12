from instrumented_test import Device
from testing.directory_paths import *


# Generic commands


def gradlew_command(dir_path: str, module_name: str, command: str):
    return f'{dir_path}/{GRADLEW} -p {dir_path}/{module_name} {command}'


def uninstall_apk_command(package_name: str, device: Device):
    return f'{ADB} -s {device.device_id} uninstall {package_name}'


def uninstall_android_test_apk_command(package_name: str, device: Device):
    return f'{ADB} -s {device.device_id} uninstall {package_name}.test'


def install_apk_command(dir_path: str, module_name: str, build_type: str, device: Device):
    return f'{ADB} -s {device.device_id} install -t -d -r ' \
           f'{dir_path}/{module_name}/build/outputs/apk/{build_type}/{module_name}-{build_type}.apk'


def install_android_test_apk_command(dir_path: str, module_name: str, build_type: str, device: Device):
    return f'{ADB} -s {device.device_id} install -t -d -r ' \
           f'{dir_path}/' \
           f'{module_name}/build/outputs/apk/androidTest/{build_type}/{module_name}-{build_type}-androidTest.apk'


def query_devices():
    return f'{ADB} devices -l'


# Specific implementations


def simprints_id_test_command(device: Device, test: str):
    return f'{ADB} -s {device.device_id} shell am instrument -w ' \
           f'-e class {test} com.simprints.id.test/android.support.test.runner.AndroidJUnitRunner '


def cerberus_app_gradlew_command(command: str):
    return gradlew_command(CERBERUS_DIR_PATH, CERBERUS_APP_MODULE_NAME, command)


def cerberus_app_uninstall_apk_command(device: Device):
    return uninstall_apk_command(CERBERUS_APP_PACKAGE_NAME, device)


def cerberus_app_install_apk_command(build_type: str, device: Device):
    return install_apk_command(CERBERUS_DIR_PATH, CERBERUS_APP_MODULE_NAME, build_type, device)


def simprints_id_gradlew_command(command: str):
    return gradlew_command(SIMPRINTS_ID_DIR_PATH, SIMPRINTS_ID_MODULE_NAME, command)


def simprints_id_uninstall_apk_command(device: Device):
    return uninstall_apk_command(SIMPRINTS_ID_PACKAGE_NAME, device)


def simprints_id_uninstall_android_test_apk_command(device: Device):
    return uninstall_android_test_apk_command(SIMPRINTS_ID_PACKAGE_NAME, device)


def simprints_id_install_apk_command(build_type: str, device: Device):
    return install_apk_command(SIMPRINTS_ID_DIR_PATH, SIMPRINTS_ID_MODULE_NAME, build_type, device)


def simprints_id_install_android_test_apk_command(build_type: str, device: Device):
    return install_android_test_apk_command(SIMPRINTS_ID_DIR_PATH, SIMPRINTS_ID_MODULE_NAME, build_type, device)