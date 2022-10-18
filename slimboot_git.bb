SUMMARY = "Slim Bootloader"
DESCRIPTION = "Slim Bootloader is an open-source boot firmware, built from the \
ground up to be small, secure and optimized running on Intel x86 architecture."
HOMEPAGE = "https://slimbootloader.github.io"

LICENSE = "BSD-2-Clause-Patent & MIT & Apache-2.0 & Python-2.0"

SRC_URI = "git://github.com/slimbootloader/slimbootloader;protocol=https;branch=master;name=slimboot"
SRCREV_slimboot = "de8ddefeb3b8b0b4e8655b35e1ac49771c9cef33"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ef7fba7be2819ac13aaf5d0f842ce5d9 \
                    file://Licenses/EDK2_License.txt;md5=6123e5bf044a66db96c4ce88a36b2d08 \
                    file://Licenses/IPP_License.txt;md5=e3fc50a88d0a364313df4b21ef20c29e \
                    file://Licenses/Lz4_License.txt;md5=093ffc6380c6b1dadf52045a6e44a874 \
                    file://Licenses/MIT_License.txt;md5=f0f3a517d46b5f0ca048b58f503b6dc1 \
                    file://Licenses/NetBSD_License.txt;md5=1811b558fd7e03c491ca7f665eaf5529 \
                    file://Licenses/Python_License.txt;md5=dd98d01d471fac8d8dbdd975229dba03 \
                   "
PV = "0.0.0+git${SRCPV}"

# Defalut target is "qemu", also support "apl tgl cml cfl ehl".
SLIMBOOT_TARGET ?= "qemu"
SLIMBOOT_KEY_DIR ?= "keys"

OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'qemu', 'slb-qemu:', '', d)}"
OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'apl', 'slb-apl:', '', d)}"
OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'tgl', 'slb-tgl:', '', d)}"
OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'cml', 'slb-cml:', '', d)}"
OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'cfl', 'slb-cfl:', '', d)}"
OVERRIDES =. "${@bb.utils.contains('SLIMBOOT_TARGET', 'ehl', 'slb-ehl:', '', d)}"

# QEMU
SRC_URI:append:slb-qemu = " git://github.com/tianocore/edk2.git;\
protocol=https;branch=master;name=qemu_edk2;destsuffix=Download/QemuSocPkg/QemuFsp"
SRCREV_qemu_edk2 = "15c596aeebc24bb97deb78d98bd8517a8b9ac243"

# Apollolake
SRC_URI:append:slb-apl = " git://github.com/intel/FSP.git;\
protocol=https;branch=master;name=apl_FSP;destsuffix=Download/ApollolakePkg/IntelFsp"
SRCREV_apl_FSP = "c80123384aa74ee0a0b011ad4e8a0afc533c8195"

# Coffeelake
SRC_URI:append:slb-cfl = " git://github.com/intel/Intel-Linux-Processor-Microcode-Data-Files.git;\
protocol=https;branch=main;name=cfl_Microcode;destsuffix=Download/CoffeelakePkg/Intel-Linux-Processor-Microcode-Data-Files"
SRCREV_cfl_Microcode = "0e4288f81f806620c65f70ee2bcf94b69d574096"

SRC_URI:append:slb-cfl = " git://github.com/intel/FSP.git;\
protocol=https;branch=master;name=cfl_FSP;destsuffix=Download/CoffeelakePkg/IntelFsp"
SRCREV_cfl_FSP = "eb25f19ef7fbe88fb207c6896a37a2035bba9bc5"

# Cometlake
SRC_URI:append:slb-cml = " git://github.com/intel/FSP.git;\
protocol=https;branch=master;name=cml_FSP;destsuffix=Download/CometlakePkg/IntelFsp"
SRCREV_cml_FSP = "2263d48a006d8653df1fc742c3f7d5ffd6b75d68"

SRC_URI:append:slb-cml = " git://github.com/tianocore/edk2-non-osi.git;\
protocol=https;branch=master;name=cml_edk2-non-osi;destsuffix=Download/CometlakePkg/edk2-non-osi"
SRCREV_cml_edk2-non-osi = "9369fc86378bc13383e0544e55dec4bf0e65a412"

# Elkhartlake
SRC_URI:append:slb-ehl= " git://github.com/intel/FSP.git;\
protocol=https;branch=master;name=ehl_FSP;destsuffix=Download/ElkhartlakePkg/IntelFsp"
SRCREV_ehl_FSP = "ccf7f35c13770174078a0492e0d95bb0afa806cb"

SRC_URI:append:slb-ehl= " git://github.com/tianocore/edk2-non-osi.git;\
protocol=https;branch=master;name=ehl_edk2-non-osi;destsuffix=Download/ElkhartlakePkg/edk2-non-osi"
SRCREV_ehl_edk2-non-osi = "3aad93ea6ac0d601d779be7ff4a2d61798cf9056"

#Tigerlake
SRC_URI:append:slb-tgl = " git://github.com/intel/FSP.git;\
protocol=https;branch=master;name=tgl_FSP;destsuffix=Download/TigerlakePkg/IntelFsp"
SRCREV_tgl_FSP = "04ad3cd76f87f44a8e632db60cecd82e881ca081"

SRC_URI:append:slb-tgl = " git://github.com/slimbootloader/firmwareblob.git;\
protocol=https;branch=master;name=tgl_firmwareblob;destsuffix=Download/TigerlakePkg/firmwareblob"
SRCREV_tgl_firmwareblob = "411cbc8e0ebb22374b7b26f14881d1013bd9fcb8"

SRCREV_FORMAT = "slimboot"

inherit python3native

DEPENDS = "openssl-native nasm-native acpica-native util-linux-native"
S = "${WORKDIR}/git"

do_configure[noexec] = "1"

do_compile() {
    # WA: To overcome direct call to "python" in scripts of slimbootloader
    ln -sf ${PYTHON} ${STAGING_BINDIR_NATIVE}/python

    cd ${S}
    rm -rf ${SLIMBOOT_KEY_DIR}; mkdir -p ${SLIMBOOT_KEY_DIR}
    export SBL_KEY_DIR=${S}/${SLIMBOOT_KEY_DIR}
    ${PYTHON} BootloaderCorePkg/Tools/GenerateKeys.py -k ${SBL_KEY_DIR}

    # Currently use EXTRA_OPTFLAGS to pass the include directory of sysroot-native to
    # bitbake build system.
    export EXTRA_OPTFLAGS="-I${STAGING_INCDIR_NATIVE}"

    export EXTRA_LDFLAGS="-L${STAGING_LIBDIR_NATIVE}"

    for target in ${SLIMBOOT_TARGET}; do
        ${PYTHON} BuildLoader.py build ${target}
    done
}

do_install() {
    for target in ${SLIMBOOT_TARGET}; do
        install -m 0755 -d ${D}${libexecdir}/slimboot/Outputs/${target}
        install -m 0755 ${S}/Outputs/${target}/* ${D}${libexecdir}/slimboot/Outputs/${target}
    done

    install -m 0644 -d ${D}${libexecdir}/slimboot/${SLIMBOOT_KEY_DIR}
    install -m 0644 ${S}/${SLIMBOOT_KEY_DIR}/* ${D}${libexecdir}/slimboot/${SLIMBOOT_KEY_DIR}
}
