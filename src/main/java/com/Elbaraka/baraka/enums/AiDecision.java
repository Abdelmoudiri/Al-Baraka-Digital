package com.Elbaraka.baraka.enums;

/**
 * Décisions de validation par l'Intelligence Artificielle
 */
public enum AiDecision {
    /**
     * L'IA recommande l'approbation automatique de l'opération
     */
    APPROVE,
    
    /**
     * L'IA recommande le rejet automatique de l'opération
     */
    REJECT,
    
    /**
     * L'IA demande une révision humaine (cas ambigus ou complexes)
     */
    NEED_HUMAN_REVIEW
}
