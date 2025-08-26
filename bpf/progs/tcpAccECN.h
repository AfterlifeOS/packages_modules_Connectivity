#pragma once

#define L4S_INGRESS_ETHER_PROG_PATH   BPF_NETD_PATH "prog_tcpAccECN_schedcls_ingress_accecn_eth"
#define L4S_EGRESS_ETHER_PROG_PATH    BPF_NETD_PATH "prog_tcpAccECN_schedcls_egress_accecn_eth"
#define L4S_INGRESS_RAWIP_PROG_PATH   BPF_NETD_PATH "prog_tcpAccECN_schedcls_ingress_accecn_rawip"
#define L4S_EGRESS_RAWIP_PROG_PATH    BPF_NETD_PATH "prog_tcpAccECN_schedcls_egress_accecn_rawip"
#define L4S_OPTIONS_SOCKOPS_PROG_PATH BPF_NETD_PATH "prog_tcpAccECN_sockops_l4s_accecn_option"
#define L4S_ACCECN_CE_MAP_PATH        BPF_NETD_PATH "map_tcpAccECN_l4s_accecn_ce_map"
#define L4S_ACCECN_BYTE_MAP_PATH      BPF_NETD_PATH "map_tcpAccECN_l4s_accecn_byte_map"
#define L4S_ACCECN_MSS_MAP_PATH       BPF_NETD_PATH "map_tcpAccECN_l4s_accecn_mss_map"

typedef struct{
    uint64_t ceb;
    uint64_t e0b;
    uint64_t e1b;
} EcnByteCounters;
STRUCT_SIZE(EcnByteCounters, 8 * 3);
