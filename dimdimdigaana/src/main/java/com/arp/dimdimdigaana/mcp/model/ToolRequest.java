package com.arp.dimdimdigaana.mcp.model;

public record ToolRequest<T>(
        String name,
        T arguments
) {}