"use client";

import { useState } from "react";
import { 
    Bold, 
    Italic, 
    Underline, 
    List, 
    ListOrdered, 
    Heading1, 
    Heading2,
    Link,
    AlignLeft,
    AlignCenter,
    AlignRight
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";

interface RichTextEditorProps {
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
    minHeight?: string;
}

export function RichTextEditor({ value, onChange, placeholder = "Enter content...", minHeight = "200px" }: RichTextEditorProps) {
    const [isLinkDialogOpen, setIsLinkDialogOpen] = useState(false);
    const [linkUrl, setLinkUrl] = useState("");

    const execCommand = (command: string, value?: string) => {
        document.execCommand(command, false, value);
        onChange(document.getElementById("editor-content")?.innerHTML || "");
    };

    const handleAddLink = () => {
        if (linkUrl) {
            execCommand("createLink", linkUrl);
            setLinkUrl("");
            setIsLinkDialogOpen(false);
        }
    };

    const ToolbarButton = ({ icon: Icon, onClick, title, isActive }: any) => (
        <Button
            type="button"
            variant="ghost"
            size="sm"
            className={cn(isActive && "bg-secondary")}
            onClick={onClick}
            title={title}
        >
            <Icon className="h-4 w-4" />
        </Button>
    );

    return (
        <Card>
            <CardContent className="p-0">
                {/* Toolbar */}
                <div className="flex items-center gap-1 p-2 border-b flex-wrap">
                    <ToolbarButton
                        icon={Bold}
                        onClick={() => execCommand("bold")}
                        title="Bold"
                    />
                    <ToolbarButton
                        icon={Italic}
                        onClick={() => execCommand("italic")}
                        title="Italic"
                    />
                    <ToolbarButton
                        icon={Underline}
                        onClick={() => execCommand("underline")}
                        title="Underline"
                    />
                    
                    <Separator orientation="vertical" className="h-6 mx-2" />
                    
                    <ToolbarButton
                        icon={Heading1}
                        onClick={() => execCommand("formatBlock", "H1")}
                        title="Heading 1"
                    />
                    <ToolbarButton
                        icon={Heading2}
                        onClick={() => execCommand("formatBlock", "H2")}
                        title="Heading 2"
                    />
                    
                    <Separator orientation="vertical" className="h-6 mx-2" />
                    
                    <ToolbarButton
                        icon={List}
                        onClick={() => execCommand("insertUnorderedList")}
                        title="Bullet List"
                    />
                    <ToolbarButton
                        icon={ListOrdered}
                        onClick={() => execCommand("insertOrderedList")}
                        title="Numbered List"
                    />
                    
                    <Separator orientation="vertical" className="h-6 mx-2" />
                    
                    <ToolbarButton
                        icon={AlignLeft}
                        onClick={() => execCommand("justifyLeft")}
                        title="Align Left"
                    />
                    <ToolbarButton
                        icon={AlignCenter}
                        onClick={() => execCommand("justifyCenter")}
                        title="Align Center"
                    />
                    <ToolbarButton
                        icon={AlignRight}
                        onClick={() => execCommand("justifyRight")}
                        title="Align Right"
                    />
                    
                    <Separator orientation="vertical" className="h-6 mx-2" />
                    
                    <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={() => setIsLinkDialogOpen(true)}
                        title="Add Link"
                    >
                        <Link className="h-4 w-4" />
                    </Button>
                </div>

                {/* Link Dialog */}
                {isLinkDialogOpen && (
                    <div className="p-2 border-b bg-secondary/50 flex gap-2 items-center">
                        <input
                            type="url"
                            value={linkUrl}
                            onChange={(e) => setLinkUrl(e.target.value)}
                            placeholder="https://example.com"
                            className="flex-1 px-3 py-1 text-sm border rounded-md"
                            onKeyPress={(e) => e.key === "Enter" && handleAddLink()}
                        />
                        <Button size="sm" onClick={handleAddLink}>
                            Add
                        </Button>
                        <Button size="sm" variant="outline" onClick={() => setIsLinkDialogOpen(false)}>
                            Cancel
                        </Button>
                    </div>
                )}

                {/* Editor */}
                <div
                    id="editor-content"
                    contentEditable
                    className="p-4 min-h-[200px] outline-none prose prose-sm max-w-none"
                    style={{ minHeight }}
                    dangerouslySetInnerHTML={{ __html: value }}
                    onInput={(e) => onChange(e.currentTarget.innerHTML)}
                    data-placeholder={placeholder}
                    aria-label={placeholder}
                />
            </CardContent>
        </Card>
    );
}
