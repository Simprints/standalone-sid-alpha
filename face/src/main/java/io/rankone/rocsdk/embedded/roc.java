/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package io.rankone.rocsdk.embedded;

public class roc implements rocConstants {
  public static byte[] cdata(SWIGTYPE_p_void ptr, int nelements) {
    return rocJNI.cdata(SWIGTYPE_p_void.getCPtr(ptr), nelements);
  }

  public static void memmove(SWIGTYPE_p_void data, byte[] indata) {
    rocJNI.memmove(SWIGTYPE_p_void.getCPtr(data), indata);
  }

  public static String roc_preinitialize_android(java.lang.Object arg0) {
    return rocJNI.roc_preinitialize_android(arg0);
  }

  public static SWIGTYPE_p_float new_float() {
    long cPtr = rocJNI.new_float();
    return (cPtr == 0) ? null : new SWIGTYPE_p_float(cPtr, false);
  }

  public static SWIGTYPE_p_float copy_float(float value) {
    long cPtr = rocJNI.copy_float(value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_float(cPtr, false);
  }

  public static void delete_float(SWIGTYPE_p_float obj) {
    rocJNI.delete_float(SWIGTYPE_p_float.getCPtr(obj));
  }

  public static void float_assign(SWIGTYPE_p_float obj, float value) {
    rocJNI.float_assign(SWIGTYPE_p_float.getCPtr(obj), value);
  }

  public static float float_value(SWIGTYPE_p_float obj) {
    return rocJNI.float_value(SWIGTYPE_p_float.getCPtr(obj));
  }

  public static SWIGTYPE_p_size_t new_size_t() {
    long cPtr = rocJNI.new_size_t();
    return (cPtr == 0) ? null : new SWIGTYPE_p_size_t(cPtr, false);
  }

  public static SWIGTYPE_p_size_t copy_size_t(long value) {
    long cPtr = rocJNI.copy_size_t(value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_size_t(cPtr, false);
  }

  public static void delete_size_t(SWIGTYPE_p_size_t obj) {
    rocJNI.delete_size_t(SWIGTYPE_p_size_t.getCPtr(obj));
  }

  public static void size_t_assign(SWIGTYPE_p_size_t obj, long value) {
    rocJNI.size_t_assign(SWIGTYPE_p_size_t.getCPtr(obj), value);
  }

  public static long size_t_value(SWIGTYPE_p_size_t obj) {
    return rocJNI.size_t_value(SWIGTYPE_p_size_t.getCPtr(obj));
  }

  public static SWIGTYPE_p_float new_roc_similarity() {
    long cPtr = rocJNI.new_roc_similarity();
    return (cPtr == 0) ? null : new SWIGTYPE_p_float(cPtr, false);
  }

  public static SWIGTYPE_p_float copy_roc_similarity(float value) {
    long cPtr = rocJNI.copy_roc_similarity(value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_float(cPtr, false);
  }

  public static void delete_roc_similarity(SWIGTYPE_p_float obj) {
    rocJNI.delete_roc_similarity(SWIGTYPE_p_float.getCPtr(obj));
  }

  public static void roc_similarity_assign(SWIGTYPE_p_float obj, float value) {
    rocJNI.roc_similarity_assign(SWIGTYPE_p_float.getCPtr(obj), value);
  }

  public static float roc_similarity_value(SWIGTYPE_p_float obj) {
    return rocJNI.roc_similarity_value(SWIGTYPE_p_float.getCPtr(obj));
  }

  public static SWIGTYPE_p_p_char new_roc_string() {
    long cPtr = rocJNI.new_roc_string();
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_char(cPtr, false);
  }

  public static SWIGTYPE_p_p_char copy_roc_string(String value) {
    long cPtr = rocJNI.copy_roc_string(value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_char(cPtr, false);
  }

  public static void delete_roc_string(SWIGTYPE_p_p_char obj) {
    rocJNI.delete_roc_string(SWIGTYPE_p_p_char.getCPtr(obj));
  }

  public static void roc_string_assign(SWIGTYPE_p_p_char obj, String value) {
    rocJNI.roc_string_assign(SWIGTYPE_p_p_char.getCPtr(obj), value);
  }

  public static String roc_string_value(SWIGTYPE_p_p_char obj) {
    return rocJNI.roc_string_value(SWIGTYPE_p_p_char.getCPtr(obj));
  }

  public static SWIGTYPE_p_p_unsigned_char new_roc_buffer() {
    long cPtr = rocJNI.new_roc_buffer();
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_unsigned_char(cPtr, false);
  }

  public static SWIGTYPE_p_p_unsigned_char copy_roc_buffer(SWIGTYPE_p_unsigned_char value) {
    long cPtr = rocJNI.copy_roc_buffer(SWIGTYPE_p_unsigned_char.getCPtr(value));
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_unsigned_char(cPtr, false);
  }

  public static void delete_roc_buffer(SWIGTYPE_p_p_unsigned_char obj) {
    rocJNI.delete_roc_buffer(SWIGTYPE_p_p_unsigned_char.getCPtr(obj));
  }

  public static void roc_buffer_assign(SWIGTYPE_p_p_unsigned_char obj, SWIGTYPE_p_unsigned_char value) {
    rocJNI.roc_buffer_assign(SWIGTYPE_p_p_unsigned_char.getCPtr(obj), SWIGTYPE_p_unsigned_char.getCPtr(value));
  }

  public static SWIGTYPE_p_unsigned_char roc_buffer_value(SWIGTYPE_p_p_unsigned_char obj) {
    long cPtr = rocJNI.roc_buffer_value(SWIGTYPE_p_p_unsigned_char.getCPtr(obj));
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public static SWIGTYPE_p_unsigned_long_long new_roc_time() {
    long cPtr = rocJNI.new_roc_time();
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_long_long(cPtr, false);
  }

  public static SWIGTYPE_p_unsigned_long_long copy_roc_time(java.math.BigInteger value) {
    long cPtr = rocJNI.copy_roc_time(value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_long_long(cPtr, false);
  }

  public static void delete_roc_time(SWIGTYPE_p_unsigned_long_long obj) {
    rocJNI.delete_roc_time(SWIGTYPE_p_unsigned_long_long.getCPtr(obj));
  }

  public static void roc_time_assign(SWIGTYPE_p_unsigned_long_long obj, java.math.BigInteger value) {
    rocJNI.roc_time_assign(SWIGTYPE_p_unsigned_long_long.getCPtr(obj), value);
  }

  public static java.math.BigInteger roc_time_value(SWIGTYPE_p_unsigned_long_long obj) {
    return rocJNI.roc_time_value(SWIGTYPE_p_unsigned_long_long.getCPtr(obj));
  }

  public static SWIGTYPE_p_unsigned_char new_uint8_t_array(int nelements) {
    long cPtr = rocJNI.new_uint8_t_array(nelements);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public static void delete_uint8_t_array(SWIGTYPE_p_unsigned_char ary) {
    rocJNI.delete_uint8_t_array(SWIGTYPE_p_unsigned_char.getCPtr(ary));
  }

  public static short uint8_t_array_getitem(SWIGTYPE_p_unsigned_char ary, int index) {
    return rocJNI.uint8_t_array_getitem(SWIGTYPE_p_unsigned_char.getCPtr(ary), index);
  }

  public static void uint8_t_array_setitem(SWIGTYPE_p_unsigned_char ary, int index, short value) {
    rocJNI.uint8_t_array_setitem(SWIGTYPE_p_unsigned_char.getCPtr(ary), index, value);
  }

  public static roc_template new_roc_template_array(int nelements) {
    long cPtr = rocJNI.new_roc_template_array(nelements);
    return (cPtr == 0) ? null : new roc_template(cPtr, false);
  }

  public static void delete_roc_template_array(roc_template ary) {
    rocJNI.delete_roc_template_array(roc_template.getCPtr(ary), ary);
  }

  public static roc_template roc_template_array_getitem(roc_template ary, int index) {
    return new roc_template(rocJNI.roc_template_array_getitem(roc_template.getCPtr(ary), ary, index), true);
  }

  public static void roc_template_array_setitem(roc_template ary, int index, roc_template value) {
    rocJNI.roc_template_array_setitem(roc_template.getCPtr(ary), ary, index, roc_template.getCPtr(value), value);
  }

  public static SWIGTYPE_p_float new_roc_similarity_array(int nelements) {
    long cPtr = rocJNI.new_roc_similarity_array(nelements);
    return (cPtr == 0) ? null : new SWIGTYPE_p_float(cPtr, false);
  }

  public static void delete_roc_similarity_array(SWIGTYPE_p_float ary) {
    rocJNI.delete_roc_similarity_array(SWIGTYPE_p_float.getCPtr(ary));
  }

  public static float roc_similarity_array_getitem(SWIGTYPE_p_float ary, int index) {
    return rocJNI.roc_similarity_array_getitem(SWIGTYPE_p_float.getCPtr(ary), index);
  }

  public static void roc_similarity_array_setitem(SWIGTYPE_p_float ary, int index, float value) {
    rocJNI.roc_similarity_array_setitem(SWIGTYPE_p_float.getCPtr(ary), index, value);
  }

  public static roc_uuid new_roc_person_id_array(int nelements) {
    long cPtr = rocJNI.new_roc_person_id_array(nelements);
    return (cPtr == 0) ? null : new roc_uuid(cPtr, false);
  }

  public static void delete_roc_person_id_array(roc_uuid ary) {
    rocJNI.delete_roc_person_id_array(roc_uuid.getCPtr(ary), ary);
  }

  public static roc_uuid roc_person_id_array_getitem(roc_uuid ary, int index) {
    return new roc_uuid(rocJNI.roc_person_id_array_getitem(roc_uuid.getCPtr(ary), ary, index), true);
  }

  public static void roc_person_id_array_setitem(roc_uuid ary, int index, roc_uuid value) {
    rocJNI.roc_person_id_array_setitem(roc_uuid.getCPtr(ary), ary, index, roc_uuid.getCPtr(value), value);
  }

  public static roc_detection new_roc_detection_array(int nelements) {
    long cPtr = rocJNI.new_roc_detection_array(nelements);
    return (cPtr == 0) ? null : new roc_detection(cPtr, false);
  }

  public static void delete_roc_detection_array(roc_detection ary) {
    rocJNI.delete_roc_detection_array(roc_detection.getCPtr(ary), ary);
  }

  public static roc_detection roc_detection_array_getitem(roc_detection ary, int index) {
    return new roc_detection(rocJNI.roc_detection_array_getitem(roc_detection.getCPtr(ary), ary, index), true);
  }

  public static void roc_detection_array_setitem(roc_detection ary, int index, roc_detection value) {
    rocJNI.roc_detection_array_setitem(roc_detection.getCPtr(ary), ary, index, roc_detection.getCPtr(value), value);
  }

  public static roc_embedded_landmark new_roc_embedded_landmark_array(int nelements) {
    long cPtr = rocJNI.new_roc_embedded_landmark_array(nelements);
    return (cPtr == 0) ? null : new roc_embedded_landmark(cPtr, false);
  }

  public static void delete_roc_embedded_landmark_array(roc_embedded_landmark ary) {
    rocJNI.delete_roc_embedded_landmark_array(roc_embedded_landmark.getCPtr(ary), ary);
  }

  public static roc_embedded_landmark roc_embedded_landmark_array_getitem(roc_embedded_landmark ary, int index) {
    return new roc_embedded_landmark(rocJNI.roc_embedded_landmark_array_getitem(roc_embedded_landmark.getCPtr(ary), ary, index), true);
  }

  public static void roc_embedded_landmark_array_setitem(roc_embedded_landmark ary, int index, roc_embedded_landmark value) {
    rocJNI.roc_embedded_landmark_array_setitem(roc_embedded_landmark.getCPtr(ary), ary, index, roc_embedded_landmark.getCPtr(value), value);
  }

  public static int roc_version_major() {
    return rocJNI.roc_version_major();
  }

  public static int roc_version_minor() {
    return rocJNI.roc_version_minor();
  }

  public static int roc_version_patch() {
    return rocJNI.roc_version_patch();
  }

  public static String roc_version_string() {
    return rocJNI.roc_version_string();
  }

  public static String roc_copyright() {
    return rocJNI.roc_copyright();
  }

  public static void roc_ensure(String error) {
    rocJNI.roc_ensure(error);
  }

  public static String roc_set_logging(boolean stdout_, String filename, SWIGTYPE_p_f_p_q_const__char__void callback) {
    return rocJNI.roc_set_logging(stdout_, filename, SWIGTYPE_p_f_p_q_const__char__void.getCPtr(callback));
  }

  public static boolean roc_log(String message) {
    return rocJNI.roc_log(message);
  }

  public static void roc_uuid_set(roc_uuid uuid, byte[] input_byte_array) {
    rocJNI.roc_uuid_set(roc_uuid.getCPtr(uuid), uuid, input_byte_array);
  }

  public static void roc_uuid_set_int(roc_uuid uuid, java.math.BigInteger val) {
    rocJNI.roc_uuid_set_int(roc_uuid.getCPtr(uuid), uuid, val);
  }

  public static roc_uuid roc_uuid_get_int(java.math.BigInteger val) {
    return new roc_uuid(rocJNI.roc_uuid_get_int(val), true);
  }

  public static java.math.BigInteger roc_uuid_to_int(roc_uuid uuid) {
    return rocJNI.roc_uuid_to_int(roc_uuid.getCPtr(uuid), uuid);
  }

  public static void roc_uuid_set_null(roc_uuid uuid) {
    rocJNI.roc_uuid_set_null(roc_uuid.getCPtr(uuid), uuid);
  }

  public static roc_uuid roc_uuid_get_null() {
    return new roc_uuid(rocJNI.roc_uuid_get_null(), true);
  }

  public static boolean roc_uuid_is_null(roc_uuid uuid) {
    return rocJNI.roc_uuid_is_null(roc_uuid.getCPtr(uuid), uuid);
  }

  public static boolean roc_uuid_is_equal(roc_uuid a, roc_uuid b) {
    return rocJNI.roc_uuid_is_equal(roc_uuid.getCPtr(a), a, roc_uuid.getCPtr(b), b);
  }

  public static boolean roc_uuid_is_less_than(roc_uuid a, roc_uuid b) {
    return rocJNI.roc_uuid_is_less_than(roc_uuid.getCPtr(a), a, roc_uuid.getCPtr(b), b);
  }

  public static void roc_hash_set(roc_hash hash, byte[] input_byte_array) {
    rocJNI.roc_hash_set(roc_hash.getCPtr(hash), hash, input_byte_array);
  }

  public static void roc_hash_set_null(roc_hash hash) {
    rocJNI.roc_hash_set_null(roc_hash.getCPtr(hash), hash);
  }

  public static boolean roc_hash_is_null(roc_hash hash) {
    return rocJNI.roc_hash_is_null(roc_hash.getCPtr(hash), hash);
  }

  public static boolean roc_hash_is_equal(roc_hash a, roc_hash b) {
    return rocJNI.roc_hash_is_equal(roc_hash.getCPtr(a), a, roc_hash.getCPtr(b), b);
  }

  public static boolean roc_hash_is_less_than(roc_hash a, roc_hash b) {
    return rocJNI.roc_hash_is_less_than(roc_hash.getCPtr(a), a, roc_hash.getCPtr(b), b);
  }

  public static roc_hash roc_uuid_to_hash(roc_uuid uuid) {
    return new roc_hash(rocJNI.roc_uuid_to_hash(roc_uuid.getCPtr(uuid), uuid), true);
  }

  public static roc_uuid roc_hash_to_uuid(roc_hash hash) {
    return new roc_uuid(rocJNI.roc_hash_to_uuid(roc_hash.getCPtr(hash), hash), true);
  }

  public static String roc_new_image(long width, long height, long step, int color_space, roc_hash media_id, java.math.BigInteger timestamp, byte[] input_byte_array, roc_image image) {
    return rocJNI.roc_new_image(width, height, step, color_space, roc_hash.getCPtr(media_id), media_id, timestamp, input_byte_array, roc_image.getCPtr(image), image);
  }

  public static String roc_copy_image(roc_image src, roc_image dst) {
    return rocJNI.roc_copy_image(roc_image.getCPtr(src), src, roc_image.getCPtr(dst), dst);
  }

  public static String roc_rotate(roc_image image, int degrees) {
    return rocJNI.roc_rotate(roc_image.getCPtr(image), image, degrees);
  }

  public static String roc_swap_channels(roc_image image) {
    return rocJNI.roc_swap_channels(roc_image.getCPtr(image), image);
  }

  public static String roc_bgr2gray(roc_image src, roc_image dst) {
    return rocJNI.roc_bgr2gray(roc_image.getCPtr(src), src, roc_image.getCPtr(dst), dst);
  }

  public static String roc_to_rgba(roc_image src, byte[] output_byte_array) {
    return rocJNI.roc_to_rgba(roc_image.getCPtr(src), src, output_byte_array);
  }

  public static String roc_from_rgba(byte[] input_byte_array, long width, long height, long step, roc_image image) {
    return rocJNI.roc_from_rgba(input_byte_array, width, height, step, roc_image.getCPtr(image), image);
  }

  public static String roc_from_bgra(byte[] input_byte_array, long width, long height, long step, roc_image image) {
    return rocJNI.roc_from_bgra(input_byte_array, width, height, step, roc_image.getCPtr(image), image);
  }

  public static String roc_from_yuv(byte[] y_in, byte[] u_in, byte[] v_in, long y_row_stride, long uv_row_stride, long uv_pixel_stride, long width, long height, roc_image image) {
    return rocJNI.roc_from_yuv(y_in, u_in, v_in, y_row_stride, uv_row_stride, uv_pixel_stride, width, height, roc_image.getCPtr(image), image);
  }

  public static String roc_read_ppm(String file_name, roc_image image) {
    return rocJNI.roc_read_ppm(file_name, roc_image.getCPtr(image), image);
  }

  public static String roc_free_image(roc_image image) {
    return rocJNI.roc_free_image(roc_image.getCPtr(image), image);
  }

  public static String roc_set_string(String src, SWIGTYPE_p_p_char dst) {
    return rocJNI.roc_set_string(src, SWIGTYPE_p_p_char.getCPtr(dst));
  }

  public static String roc_free_string(SWIGTYPE_p_p_char str) {
    return rocJNI.roc_free_string(SWIGTYPE_p_p_char.getCPtr(str));
  }

  public static String roc_free_buffer(SWIGTYPE_p_p_unsigned_char buffer) {
    return rocJNI.roc_free_buffer(SWIGTYPE_p_p_unsigned_char.getCPtr(buffer));
  }

  public static String roc_pose_to_string(long pose) {
    return rocJNI.roc_pose_to_string(pose);
  }

  public static String roc_landmarks_to_detection(float right_eye_x, float right_eye_y, float left_eye_x, float left_eye_y, float chin_x, float chin_y, roc_detection detection) {
    return rocJNI.roc_landmarks_to_detection(right_eye_x, right_eye_y, left_eye_x, left_eye_y, chin_x, chin_y, roc_detection.getCPtr(detection), detection);
  }

  public static String roc_adaptive_minimum_size(roc_image image, float relative_minimum_size, long absolute_minimum_size, SWIGTYPE_p_size_t adaptive_minimum_size) {
    return rocJNI.roc_adaptive_minimum_size(roc_image.getCPtr(image), image, relative_minimum_size, absolute_minimum_size, SWIGTYPE_p_size_t.getCPtr(adaptive_minimum_size));
  }

  public static String roc_check_template_version(long algorithm_id) {
    return rocJNI.roc_check_template_version(algorithm_id);
  }

  public static long getRoc_template_header_size() {
    return rocJNI.roc_template_header_size_get();
  }

  public static String roc_free_template(roc_template template_) {
    return rocJNI.roc_free_template(roc_template.getCPtr(template_), template_);
  }

  public static String roc_copy_template(roc_template src, roc_template dst) {
    return rocJNI.roc_copy_template(roc_template.getCPtr(src), src, roc_template.getCPtr(dst), dst);
  }

  public static String roc_flatten(roc_template template_, byte[] output_byte_array) {
    return rocJNI.roc_flatten(roc_template.getCPtr(template_), template_, output_byte_array);
  }

  public static String roc_unflatten(byte[] input_byte_array, roc_template template_) {
    return rocJNI.roc_unflatten(input_byte_array, roc_template.getCPtr(template_), template_);
  }

  public static String roc_flattened_bytes(roc_template template_, SWIGTYPE_p_size_t bytes) {
    return rocJNI.roc_flattened_bytes(roc_template.getCPtr(template_), template_, SWIGTYPE_p_size_t.getCPtr(bytes));
  }

  public static SWIGTYPE_p_void roc_cast(SWIGTYPE_p_unsigned_char pointer) {
    long cPtr = rocJNI.roc_cast(SWIGTYPE_p_unsigned_char.getCPtr(pointer));
    return (cPtr == 0) ? null : new SWIGTYPE_p_void(cPtr, false);
  }

  public static String roc_fuse(SWIGTYPE_p_float raw, long n, SWIGTYPE_p_float fused) {
    return rocJNI.roc_fuse(SWIGTYPE_p_float.getCPtr(raw), n, SWIGTYPE_p_float.getCPtr(fused));
  }

  public static String roc_enable_openmp(boolean enabled) {
    return rocJNI.roc_enable_openmp(enabled);
  }

  public static int roc_embedded_initialize(String license_file) {
    return rocJNI.roc_embedded_initialize(license_file);
  }

  public static int roc_embedded_finalize() {
    return rocJNI.roc_embedded_finalize();
  }

  public static String roc_embedded_error_to_string(int error) {
    return rocJNI.roc_embedded_error_to_string(error);
  }

  public static int roc_embedded_detect_faces(roc_image image, long min_size, int k, float false_detection_rate, SWIGTYPE_p_size_t n, roc_detection detections) {
    return rocJNI.roc_embedded_detect_faces(roc_image.getCPtr(image), image, min_size, k, false_detection_rate, SWIGTYPE_p_size_t.getCPtr(n), roc_detection.getCPtr(detections), detections);
  }

  public static int roc_embedded_landmark_face(roc_image image, roc_detection detection, roc_embedded_landmark landmarks, roc_embedded_landmark right_eye, roc_embedded_landmark left_eye, roc_embedded_landmark chin, SWIGTYPE_p_float pitch, SWIGTYPE_p_float yaw) {
    return rocJNI.roc_embedded_landmark_face(roc_image.getCPtr(image), image, roc_detection.getCPtr(detection), detection, roc_embedded_landmark.getCPtr(landmarks), landmarks, roc_embedded_landmark.getCPtr(right_eye), right_eye, roc_embedded_landmark.getCPtr(left_eye), left_eye, roc_embedded_landmark.getCPtr(chin), chin, SWIGTYPE_p_float.getCPtr(pitch), SWIGTYPE_p_float.getCPtr(yaw));
  }

  public static int roc_embedded_liveness(roc_image image, roc_embedded_landmark landmarks, boolean fixed_focus, SWIGTYPE_p_float spoof) {
    return rocJNI.roc_embedded_liveness(roc_image.getCPtr(image), image, roc_embedded_landmark.getCPtr(landmarks), landmarks, fixed_focus, SWIGTYPE_p_float.getCPtr(spoof));
  }

  public static int roc_embedded_represent_face(roc_image image, roc_detection detection, roc_embedded_landmark right_eye, roc_embedded_landmark left_eye, roc_embedded_landmark chin, SWIGTYPE_p_unsigned_char feature_vector, SWIGTYPE_p_float quality, SWIGTYPE_p_float age, roc_embedded_gender gender, roc_embedded_geographic_origin geographic_origin, roc_embedded_glasses glasses) {
    return rocJNI.roc_embedded_represent_face(roc_image.getCPtr(image), image, roc_detection.getCPtr(detection), detection, roc_embedded_landmark.getCPtr(right_eye), right_eye, roc_embedded_landmark.getCPtr(left_eye), left_eye, roc_embedded_landmark.getCPtr(chin), chin, SWIGTYPE_p_unsigned_char.getCPtr(feature_vector), SWIGTYPE_p_float.getCPtr(quality), SWIGTYPE_p_float.getCPtr(age), roc_embedded_gender.getCPtr(gender), gender, roc_embedded_geographic_origin.getCPtr(geographic_origin), geographic_origin, roc_embedded_glasses.getCPtr(glasses), glasses);
  }

  public static float roc_embedded_compare_templates(long algorithm_id, SWIGTYPE_p_unsigned_char a, long a_size, SWIGTYPE_p_unsigned_char b, long b_size) {
    return rocJNI.roc_embedded_compare_templates(algorithm_id, SWIGTYPE_p_unsigned_char.getCPtr(a), a_size, SWIGTYPE_p_unsigned_char.getCPtr(b), b_size);
  }

  public static void roc_embedded_array_initialize(roc_embedded_array array, int element_size) {
    rocJNI.roc_embedded_array_initialize(roc_embedded_array.getCPtr(array), array, element_size);
  }

  public static int roc_embedded_array_append(roc_embedded_array array, SWIGTYPE_p_void element) {
    return rocJNI.roc_embedded_array_append(roc_embedded_array.getCPtr(array), array, SWIGTYPE_p_void.getCPtr(element));
  }

  public static void roc_embedded_array_get(roc_embedded_array array, int index, SWIGTYPE_p_void element) {
    rocJNI.roc_embedded_array_get(roc_embedded_array.getCPtr(array), array, index, SWIGTYPE_p_void.getCPtr(element));
  }

  public static void roc_embedded_array_free(roc_embedded_array array) {
    rocJNI.roc_embedded_array_free(roc_embedded_array.getCPtr(array), array);
  }

  public static long roc_embedded_checksum(SWIGTYPE_p_unsigned_char data, int size) {
    return rocJNI.roc_embedded_checksum(SWIGTYPE_p_unsigned_char.getCPtr(data), size);
  }

}