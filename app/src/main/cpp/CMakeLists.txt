cmake_minimum_required(VERSION 3.22.1)

add_subdirectory(winlator)
add_subdirectory(vortekrenderer)
add_subdirectory(virglrenderer)
# add_subdirectory(midihandler) # disabled: links ARM64 .so incompatible with ARM32
add_subdirectory(gladiorenderer)