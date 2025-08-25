#pragma once

// This header file is shared by eBPF kernel programs (C) and netd (C++) and
// some of the maps are also accessed directly from Java mainline module code.
//
// Hence: explicitly pad all relevant structures and assert that their size
// is the sum of the sizes of their fields.
#define STRUCT_SIZE(name, size) _Static_assert(sizeof(name) == (size), "Incorrect struct size.")

#define SS_BPF_PATH "/sys/fs/bpf/"

#define L4S_INGRESS_ETHER_PROG_PATH SS_BPF_PATH "prog_tcpAccECN_schedcls_ingress_l4s_accecn_eth"
#define L4S_EGRESS_ETHER_PROG_PATH SS_BPF_PATH "prog_tcpAccECN_schedcls_egress_l4s_accecn_eth"
#define L4S_INGRESS_RAWIP_PROG_PATH SS_BPF_PATH "prog_tcpAccECN_schedcls_ingress_l4s_accecn_rawip"
#define L4S_EGRESS_RAWIP_PROG_PATH SS_BPF_PATH "prog_tcpAccECN_schedcls_egress_l4s_accecn_rawip"
#define L4S_OPTIONS_SOCKOPS_PROG_PATH SS_BPF_PATH "prog_tcpAccECN_sockops_l4s_accecn_option"
#define L4S_ACCECN_CE_MAP_PATH SS_BPF_PATH "map_tcpAccECN_l4s_accecn_ce_map"
#define L4S_ACCECN_BYTE_MAP_PATH SS_BPF_PATH "map_tcpAccECN_l4s_accecn_byte_map"
#define L4S_ACCECN_MSS_MAP_PATH SS_BPF_PATH "map_tcpAccECN_l4s_accecn_mss_map"

typedef struct{
    uint64_t ceb;
    uint64_t e0b;
    uint64_t e1b;
} EcnByteCounters;
STRUCT_SIZE(EcnByteCounters, 8 * 3);
